博客文章:[动态日志实践](http://dim.red/2018/05/03/dylog_exploration/)
# 关于
因为一些因素导致 zzz40500 的账号不可用了. 现在会将所有的代码从 [zzz40500](https://github.com/zzz40500/GsonFormat) 迁出至 [DimRed](https://github.com/DimRed/GsonFormat).  所有的维护以及更新也会在新的项目目录下进行. 
# 使用
### groovy
/app/build.gradle

~~~
apply plugin: 'monitor.plugin'  

monitor {
    // 注入所有red.dim.dynamiclog 包名下的class
    packageList "red.dim.dynamiclog"
    // 不注入 red.dim.dynamiclog.App class
    blackPackageList "red.dim.dynamiclog.App"
}
~~~
Api:
packageList 注入的包名
blackPackageList 黑名单, 在这里的不会被注入.

### java

初始化

~~~
       List<IMethodMonitor> methodListeners = new ArrayList<>();
        methodListeners.add(new IMethodMonitor() {
            @Override
            public void methodEnter(Point point) {
                // 方法前调用
                Log.d(TAG, "methodEnter: " + point);
            }

            @Override
            public void methodReturn(Point point, int methodId) {
                // 方法后调用
                Log.d(TAG, "methodReturn: " + point + " methodId: " + methodId);
            }
        });
        Monitor.setup(methodListeners);
~~~

更新匹配规则: (由网络返回)

~~~
    List<Aspect> aspects = new ArrayList<>();
    Aspect aspect = new Aspect();
    aspect.advices = new ArrayList<>();
    // class name
    aspect.target = "red.dim.dynamiclog.TestMethod";
    Advice advice = new Advice();
    advice.before = true;
    advice.after = false;
    // 从 monitorMapping.txt 中获取
    advice.methodId = methodId;
    aspect.advices.add(advice);
    aspects.add(aspect);
    Monitor.getInstance().execute(aspects, false);
~~~






