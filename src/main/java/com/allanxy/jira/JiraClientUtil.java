package com.allanxy.jira;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.IssueLink;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

public class JiraClientUtil {
    private static String url = "http://183.62.253.123:9990";
    private String username = "cocoweb";
    private String password = "cocoweb,1";

    private static JiraClient jiraClient =null;
    
    public JiraClient getJiraClient() {
        return jiraClient;
    }

    public static JiraClientUtil getInstance(String xurl,String user,String pwd) {
        JiraClientUtil jiraclientutil = new JiraClientUtil();
        jiraclientutil.url = xurl;
        jiraclientutil.password = pwd;
        jiraclientutil.username = user;
        
        if (jiraClient == null) {
            jiraClient = jiraclientutil.loginJIRA(xurl,user,pwd);
        }
        return jiraclientutil;
    }
    
    public static List<IssueLink> getIssueLinks(Issue xissue){
        try {
            return jiraClient.getIssue(xissue.getKey()).getIssueLinks();
        } catch (JiraException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }
    
    public JiraClient loginJIRA(String xurl,String user,String pwd){
        BasicCredentials creds = new BasicCredentials(user, pwd);
        
        try {
            return  new JiraClient(xurl, creds);
        } catch (JiraException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String toIssueURL(String skey){
        return url+ "/browse/"+skey;
    }

    public static String LinkToString(IssueLink issueLink ){
        Issue ais = issueLink.getOutwardIssue();
        ais = ( ais==null)?issueLink.getInwardIssue():ais;
        
        return ais.getKey()+" | "
              +ais.getSummary()+" | "
              +toIssueURL(ais.getKey());
    }

    public static void showLinks(Issue xissue){
        /* Print the issue key. */
        System.out.print(xissue);
    
        /* You can also do it like this: */
        System.out.println(xissue.getSummary());
        
        
        
        List<IssueLink> lsIssuelink= getIssueLinks(xissue);
        
        
        for(IssueLink issueLink : lsIssuelink){
            
            
            System.out.println(LinkToString(issueLink));
        }
    
    }
    
    public static Map<String,String> getIssueAllinks(Issue xissue){
        Map<String,String> xmap = new TreeMap<String, String>();
        
        addLinkToMap(xissue,xmap);
        
        
        List<Issue> listissue= xissue.getSubtasks();
        for(Issue is :listissue){
            if(is!=null){
                addLinkToMap(is,xmap);
            }
        }
        
        
        //List<IssueLink> lsIssuelink= getIssueLinks(xissue);
        
        return xmap;
        
    }
    
    private static void addLinkToMap(Issue xissue,Map<String,String> xmap){
        
        List<IssueLink> lsIssuelink= getIssueLinks(xissue);
        
        for(IssueLink issueLink : lsIssuelink){
            if(issueLink!=null){
                linkInsertToMap(issueLink,xmap);
            }
        }
        
    }
    
    private static void linkInsertToMap(IssueLink issueLink,Map<String,String> xmap){
        Issue ais = issueLink.getOutwardIssue();
        ais = ( ais==null)?issueLink.getInwardIssue():ais;
        xmap.put(ais.getKey(), ais.getStatus()+" << "+ais.getSummary());
        
    }

}
