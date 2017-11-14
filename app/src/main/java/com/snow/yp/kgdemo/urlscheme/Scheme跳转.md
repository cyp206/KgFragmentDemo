### Scheme唤起

>Scheme是什么

​	Android中的scheme是一种页面内跳转的协议。

​	场景：1、外部app唤起 

​		    2、html  < a href =""/>唤起

​		    3、内部跳转也可以采用此方法



> 自定义Scheme 

~~~
栗子1：
http://baidu:8080/news?system=pc&id=45464
 
http 代表Scheme的协议

baidu代表作用于哪个地址域

8080代表路径的端口号

news代表Scheme指定的页面

system和id代表要传递的参数

对应的AndroidManifest
<intent-filter>
 	 <data
       android:host="zi"
       android:path="/tiam"
       android:port="8888"
       android:scheme="mu" />
      <!--BROWSABLE指定该Activity能被浏览器安全调用-->
      <category android:name="android.intent.category.DEFAULT" />
      <action android:name="android.intent.action.VIEW" />
      <!--声明自定义scheme，类似于http, https-->
  <category android:name="android.intent.category.BROWSABLE" /> <intent-filter>
 
 
action、category、data都必须完全匹配才能获得intent，这里声明了2个category，只有在intent同时含有这2个category时才算匹配，而android.intent.category.DEFAULT是默认的，有实际意义的是android.intent.category.BROWSABLE，表示允许通过浏览器启动该activity（呼起App）。后续的data限定了触发条件，当scheme为hoho时才匹配，例如浏览器访问hoho://abc，能够匹配成功，App就起来了
~~~



>跳转

~~~html
<-->html：</-->
<a href="mu://zi:8888/tiam?uid=1001>唤起</a>
~~~

~~~java
 /**
 *Activity：
 * mu://zi:9527/index
 */
 Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mu://zi:8888/tiam?uid=1001"));
                startActivity(intent);
~~~



>取数据

~~~java
 // 获取uri参数
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        Uri uri = intent.getData();
        if (uri != null) {
            String host = uri.getHost();
            String dataString = intent.getDataString();
            String from = uri.getQueryParameter("from");
            String path = uri.getPath();
            String encodedPath = uri.getEncodedPath();
            String queryString = uri.getQuery();
        }
~~~





>参考

http://www.ayqy.net/blog/android-scheme%E5%91%BC%E8%B5%B7app/