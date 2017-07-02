package com.foresee.xdeploy.tmp;
/*
 * JIRA是一个缺陷跟踪管理系统，被广泛应用于缺陷跟踪、客户服务、需求收集、流程审批、任务跟踪、项目跟踪和敏捷管理等工作领域,当我们需要把第三方业务系统集成进来时，可以调用他的API。

JIRA本身的API非常强大，但它是一个底层的API体系，并不是一个易用的接口，如果要开发和拓展，所以需要我们二次包装。

jira官方为解决这个问题，推出了方便强大的java client library（目前只有java客户端库，没有.Net类库）

jira的Rest API  最新文档官网.

JIRA 6.4.12 REST API documentation
https://docs.atlassian.com/jira/REST/latest/

JIRA REST API Tutorials：
https://developer.atlassian.com/jiradev/jira-apis/jira-rest-apis/jira-rest-api-tutorials

 

如果是编写java桌面或web应用，jira提供了更方便的方式（Client类库），JIRA REST Java Client is a Java library (usable from any JVM language) which allows to easily talk to any JIRA 4.2+ instance using new (and still evolving) REST API.

JIRA Java Client library 
https://ecosystem.atlassian.net/wiki/display/JRJC/Home

 

如果使用Client类库，可以方便应用各种现成的jira实体类（如项目、问题、备注、自定义字段......），不需要再重复造轮子，大幅提升效率。

 

首先，必须要了解JIRA api的接口结构，其中<resource-name>可以理解成api的方法，比如project，就是项目信息，user就是用户信息，issue就是问题信息....

1
http://hostname/rest/<api-name>/<api-version>/<resource-name>
JIRA's REST API is provided by a plugin that is anchored under the URI path component /rest/. Hence, if your JIRA site is running at:

还先要搞清楚jira api的 认证体系，摘自官网：

 the first step in using the JIRA REST API is to authenticate a user account with your JIRA site. For the purposes of this tutorial we will use HTTP BASIC Authentication, but any authentication that works against JIRA will work against the REST API. This includes:

OAuth
HTTP Cookies
Trusted Applications
os_username/os_password query parameters
为方便使用，我们采用Basic Auth

Basic Auth headers
If you need to you may construct and send basic auth headers yourself. To do this you need to perform the following steps:

Build a string of the form username:password
Base64 encode the string
Supply an "Authorization" header with content "Basic " followed by the encoded string. For example, the string "fred:fred" encodes to "ZnJlZDpmcmVk" in base64, so you would make the request as follows.
一个curl的例子，注意红色字符串是对“username:password”的Base64编码
curl -D- -X GET -H "Authorization: Basic ZnJlZDpmcmVk" -H "Content-Type: application/json" "http://kelpie9:8081/rest/api/2/issue/QA-31"
JAVA Client类库实现的API DEMO
 * 
*/
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.codec.binary.Base32;
import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.SearchRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.BasicUser;
import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.Field;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.google.common.collect.Lists;

public class CvteJiraDemo {

    public static String BaseURL = "http://jira-test:8080/";
    public static String User = "admin";
    public static String Password = "admin";
    private static URI jiraServerUri = URI
            .create("http://jira-test:8080/rest/api/2/");
    private static boolean quiet = false;
    private static final long BUG_TYPE_ID = 1L; // JIRA magic value
    private static final long TASK_TYPE_ID = 3L; // JIRA magic value
    private static final DateTime DUE_DATE = new DateTime();
    private static final String PRIORITY = "Trivial";
    private static final String DESCRIPTION = "description";

    

    public static void main(String[] args) throws InterruptedException,
            ExecutionException {

        final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        URI jiraServerUri;
        try {
            jiraServerUri = new URI(BaseURL);
            final JiraRestClient restClient = (JiraRestClient) factory
                    .createWithBasicHttpAuthentication(jiraServerUri, User,
                            Password);
            getAllProjects(restClient);
            getProject(restClient, "DEMO");
            getIssue(restClient, "FEEDBACK-14");
            getIssueFields(restClient, "FEEDBACK-27");
            addIssue(restClient, "FEEDBACK", "AAAAB");
            addIssueComplex(restClient, "FEEDBACK",DUE_DATE.toString());

        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

        }
    }
    
    private static void println(Object o) {
        if (!quiet) {
            System.out.println(o);
        }
    }

    private static void parseArgs(String[] argsArray) throws URISyntaxException {
        final List<String> args = Lists.newArrayList(argsArray);
        if (args.contains("-q")) {
            quiet = true;
            args.remove(args.indexOf("-q"));
        }

        if (!args.isEmpty()) {
            jiraServerUri = new URI(args.get(0));
        }
    }

    private static Transition getTransitionByName(
            Iterable<Transition> transitions, String transitionName) {
        for (Transition transition : transitions) {
            if (transition.getName().equals(transitionName)) {
                return transition;
            }
        }
        return null;
    }

    // 得到所有项目信息
    private static void getAllProjects(final JiraRestClient restClient)
            throws InterruptedException, ExecutionException {
        try {

            Promise<Iterable<BasicProject>> list = restClient
                    .getProjectClient().getAllProjects();
            Iterable<BasicProject> a = list.get();
            Iterator<BasicProject> it = a.iterator();
            while (it.hasNext()) {
                System.out.println(it.next());
            }

        } finally {
        }
    }

    // 得到单一项目信息
    private static void getProject(final JiraRestClient restClient,
            String porjectKEY) throws InterruptedException, ExecutionException {
        try {

            Project project = restClient.getProjectClient()
                    .getProject(porjectKEY).get();
            System.out.println(project);

        } finally {
        }
    }

    // 得到单一问题信息
    private static void getIssue(final JiraRestClient restClient,
            String issueKEY) throws InterruptedException, ExecutionException {
        try {

            Promise<Issue> list = restClient.getIssueClient()
                    .getIssue(issueKEY);
            Issue issue = list.get();
            System.out.println(issue);

        } finally {
        }
    }
    
    // 创建问题
    public static BasicIssue createIssue(final JiraRestClient jiraRestClient,
            IssueInput newIssue) {
        BasicIssue basicIssue = jiraRestClient.getIssueClient()
                .createIssue(newIssue).claim();
        return basicIssue;
    }
    
    //添加备注到问题
    public static void addCommentToIssue(final JiraRestClient jiraRestClient,Issue issue, String comment) {
        IssueRestClient issueClient = jiraRestClient.getIssueClient();
        issueClient.addComment(issue.getCommentsUri(), Comment.valueOf(comment)).claim();
    }
    
    
    //删除问题，目前找不到对应API
    public static void deleteIssue(final JiraRestClient jiraRestClient, Issue issue) {
        IssueRestClient issueClient = jiraRestClient.getIssueClient();
        //issueClient.deleteIssue(issue.getKey(), false).claim();
    }

    //通过标题获取问题
    public static Iterable findIssuesByLabel(final JiraRestClient jiraRestClient, String label) {
        SearchRestClient searchClient = jiraRestClient.getSearchClient();
        String jql = "labels%3D"+label;
        com.atlassian.jira.rest.client.domain.SearchResult results = ((SearchRestClient) jiraRestClient).searchJql(jql).claim();
        return results.getIssues();
    }

    //通过KEY获取问题
    public static Issue findIssueByIssueKey(final JiraRestClient jiraRestClient, String issueKey) {
        SearchRestClient searchClient = jiraRestClient.getSearchClient();
        String jql = "issuekey = \"" + issueKey + "\"";
        SearchResult results = searchClient.searchJql(jql).claim();
        return (Issue) results.getIssues().iterator().next();
    }

    // 创建问题 ：仅有简单问题名称
    private static void addIssue(final JiraRestClient restClient,
            String porjectKEY, String issueName) throws InterruptedException,
            ExecutionException {
        try {
            IssueInputBuilder builder = new IssueInputBuilder(porjectKEY,
                    TASK_TYPE_ID, issueName);
            builder.setDescription("issue description");
            final IssueInput input = builder.build();

            try {
                // create issue
                final IssueRestClient client = restClient.getIssueClient();
                final BasicIssue issue = client.createIssue(input).claim();
                final Issue actual = client.getIssue(issue.getKey()).claim();
                System.out.println(actual);
            } finally {
                if (restClient != null) {
                    // restClient.close();
                }
            }

        } finally {
        }
    }

    // 创建问题 ：包含自定义字段
    private static void addIssueComplex(final JiraRestClient restClient,
            String porjectKEY, String issueName) throws InterruptedException,
            ExecutionException {
        try {
            IssueInputBuilder builder = new IssueInputBuilder(porjectKEY,
                    TASK_TYPE_ID, issueName);
            builder.setDescription("issue description");
            // builder.setFieldValue("priority", ComplexIssueInputFieldValue.with("name", PRIORITY));
            //单行文本
            builder.setFieldValue("customfield_10042", "单行文本测试");
            
            //单选字段
            builder.setFieldValue("customfield_10043", ComplexIssueInputFieldValue.with("value", "一般"));
            
            //数值自定义字段
            builder.setFieldValue("customfield_10044", 100.08);
            
            //用户选择自定义字段
            builder.setFieldValue("customfield_10045", ComplexIssueInputFieldValue.with("name", "admin"));
            //用户选择自定义字段(多选)
            Map<String, Object> user1 = new HashMap<String, Object>();
            user1.put("name", "admin");
            Map<String, Object> user2 = new HashMap<String, Object>();
            user2.put("name", "wangxn");            
            ArrayList peoples = new ArrayList();
            peoples.add(user1);
            peoples.add(user2);
            builder.setFieldValue("customfield_10047", peoples); 
            
            //设定父问题
            Map<String, Object> parent = new HashMap<String, Object>();
            parent.put("key", "FEEDBACK-25");
            FieldInput parentField = new FieldInput("parent", new ComplexIssueInputFieldValue(parent));
            builder.setFieldInput(parentField);

            final IssueInput input = builder.build();
            try {
                final IssueRestClient client = restClient.getIssueClient();
                final BasicIssue issue = client.createIssue(input).claim();
                final Issue actual = client.getIssue(issue.getKey()).claim();
                System.out.println(actual);
            } finally {
                if (restClient != null) {
                    // restClient.close();
                }
            }

        } finally {
        }
    }

    
    //获取问题的所有字段
    private static void getIssueFields(final JiraRestClient restClient,
            String issueKEY) throws InterruptedException, ExecutionException {
        try {

            Promise<Issue> list = restClient.getIssueClient()
                    .getIssue(issueKEY);
            Issue issue = list.get();
            Iterable<Field> fields = issue.getFields();
            Iterator<Field> it = fields.iterator();
            while (it.hasNext()) {
                System.out.println(it.next());
            }

        } finally {
        }
    }

}