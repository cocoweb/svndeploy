package com.foresee.xdeploy.tmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.foresee.test.util.io.FileUtil;
 

 
public class TemplateEngine{
 
    private static String               confPath;   // 配置文件，包括完整绝对路径
    private static String               enter;      // 换行符
 
    // 构造函数，初始化数据
    public TemplateEngine( String confPath){
        TemplateEngine.confPath = confPath;
        TemplateEngine.enter = System.getProperty("line.separator");
    }
 
    /**
     * 测试
     * 
     * @param args
     */
    public static void main(String[] args){
        Map<String, String> user = new HashMap<String, String>();
        user.put("id", "2");
        user.put("name", "小林");
        user.put("sex", "0");
        user.put("age", "26");
        user.put("description", "他是个坏淫");
        // 设置换行符
        setEnter(System.getProperty("line.separator"));
        // 读取模板文件
        String template = readTemplate("f://user.tpl");
        // 替换模板变量
        String dataString = replaceArgs(template, user);
        // 追加写入配置文件
        writeConf("f://user.conf", dataString, true);
        // 测试删除对象
        TemplateEngine te = new TemplateEngine( "f://user.conf");
        te.deleteObject("1");
    }
 
    /**
     * 读取模板文件（文件名跟配置文件相同，后缀为.tpl）
     * 
     * @return
     */
    public static String readTemplate(String tplPath){
        StringBuffer sb = new StringBuffer();
        try{
            FileReader fr = new FileReader(tplPath);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while((line = br.readLine()) != null){
                sb.append(line + enter); // 加一个换行符
            }
            br.close();
            fr.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
 
    /**
     * 替换模板变量
     * 
     * @param template
     * @param data
     * @return
     */
    public static String replaceArgs(String template, Map<String, String> data){
        // sb用来存储替换过的内容，它会把多次处理过的字符串按源字符串序 存储起来。
        StringBuffer sb = new StringBuffer();
        try{
            Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
            Matcher matcher = pattern.matcher(template);
            while(matcher.find()){
                String name = matcher.group(1);// 键名
                String value = (String)data.get(name);// 键值
                if(value == null){
                    value = "";
                }else{
                    value = value.replaceAll("\\$", "\\\\\\$");
                }
                matcher.appendReplacement(sb, value);
            }
            // 最后还得要把尾串接到已替换的内容后面去，这里尾串为“，欢迎下次光临！”
            matcher.appendTail(sb);
        }catch(Exception e){
            e.printStackTrace();
        }
        return sb.toString() + enter;   //加一个空行（结束行）
    }
 
    /**
     * 读取配置文件
     * 
     * @param confPath
     */
    public static String readConf(String confPath){
        StringBuffer sb = new StringBuffer();
        try{
            FileReader fr = new FileReader(confPath);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while((line = br.readLine()) != null){
                sb.append(line + enter);
            }
            br.close();
            fr.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
 
    /**
     * 写入到配置文件
     * 
     * @param confPath
     * @param stringData
     * @param isAppend 是否追加写入
     */
    public static void writeConf(String confPath, String stringData, boolean isAppend){
        try{
            File f = new File(confPath);
            if( !f.exists()){
                f.createNewFile();
            }
            String fileEncode = System.getProperty("file.encoding");
            FileOutputStream fos = new FileOutputStream(confPath, isAppend);
            OutputStreamWriter osw = new OutputStreamWriter(fos, fileEncode);
            osw.write(new String(stringData.getBytes(), fileEncode));
            osw.close();
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
 
    /**
     * 根据中括号内的ID查询对象
     * 
     * @param id
     * @return
     */
    public String getObject(String id){
        StringBuffer sb = new StringBuffer();
        try{
            FileReader fr = new FileReader(confPath);
            LineNumberReader nr = new LineNumberReader(fr);
            String line = "";
            int startLineNumber = -1;
            while((line = nr.readLine()) != null){
                // 匹配到开头
                if(line.indexOf("[" + id + "]") >= 0){
                    startLineNumber = nr.getLineNumber();
                }
                if(startLineNumber != -1 && nr.getLineNumber() >= startLineNumber){
                    sb.append(line + enter);
                    // 匹配到结束，以换行符结束
                    if(line.trim().equals("")){
                        break;
                    }
                }
            }
            nr.close();
            fr.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
 
    /**
     * 追加写入对象到配置文件
     * 
     * @param data
     */
    public void addObject(Map<String, String> data){
        try{
            // 读取模板文件
            String tplName = confPath.substring(confPath.lastIndexOf(File.separator) + 1, confPath.lastIndexOf(".")) + ".tpl";
            String tplPath = getTemplatePath(tplName);
            String template = readTemplate(tplPath);
            // 替换模板变量
            String stringData = replaceArgs(template, data);
            // 追加写入配置文件
            writeConf(confPath, stringData, true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
 
    private String getTemplatePath(String tplName) {
        return FileUtil.lookupFileInClasspath(File.separator + "template" + File.separator + tplName).getAbsolutePath();
    }

    /**
     * 删除对象（IO没有对文本直接删除的方法，先读出所有内容，过滤删除内容，重新写回文件。）
     * 
     * @param id
     * @return
     */
    public void deleteObject(String id){
        // 读取配置文件
        String data = readConf(confPath);
        // 过滤删除内容
        data = data.replace(getObject(id), "");
        // 重新写回文件
        writeConf(confPath, data, false);
    }
 
    /**
     * 修改对象（模板数据并非全是键值对，所以只能先删除再添加）
     * 
     * @param id
     * @param data
     */
    public void updateObject(String id, Map<String, String> data){
        // 读取配置文件
        String dataString = readConf(confPath);
        // 过滤删除内容
        dataString = dataString.replace(getObject(id), "");
        // 读取模板文件
        String tplName = confPath.substring(confPath.lastIndexOf(File.separator) + 1, confPath.lastIndexOf(".")) + ".tpl";
        String tplPath = getTemplatePath(tplName);
        String template = readTemplate(tplPath);
        // 替换模板变量
        String newData = replaceArgs(template, data);
        // 末尾追加新对象
        writeConf(confPath, dataString + newData, false);
    }
 
    public static String getConfPath(){
        return confPath;
    }
 
    public static void setConfPath(String confPath){
        TemplateEngine.confPath = confPath;
    }
 
    public static String getEnter(){
        return enter;
    }
 
    public static void setEnter(String enter){
        TemplateEngine.enter = enter;
    }
    
 
}