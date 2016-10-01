# Android应用更新

今年友盟取消了Android App更新的业务，关在论詓上提供了一篇文章：[自动更新替换方案](http://bbs.umeng.com/thread-15010-1-1.html)，至此，更新业务就只能自己研发了。

本项目提供更新App的客户端库与PHP版本的服务器代码，当然，只是简单的替代，复杂的升级程序包括：更新的实时推送，升级文件的CDN加速，动态监控升级的版本信息，精细的灰度控制等，要全面处理这些总是，则不是简单几行代码能解决的。

![Demo截图](https://github.com/shishengyi/AndroidUpdate/raw/master/Client/README/demo-screenshot.png)

# 客户端集成方式
下载代码后，/Client/AndroidUpdate下的代码即为更新程序的aar库，这个库提供了类似友盟的接口，如果之前使用友盟的自动升级程序进行更新，则移至本项目的Library工程会很轻松。
## 1，初始化更新环境
```
//在App的onCreate中设置更新程序的地址,友盟因为只有一个地址，按UMENT_KEY进行区分，所以无需设置
@Override
public void onCreate() {
    super.onCreate();

    UpdateAgent.init(this,"http://www.yourupdatedomain/");
}
```

## 2，调用接口进行更新
```
public void onCheckUpdate(){
    UpdateAgent.setUpdateListener(new UpdateListener() {
        @Override
        public void onUpdateReturned(UpdateResponse updateResponse) {
            switch(updateResponse.updateType){
                case UpdateResponse.ForceUpdate:
                        UpdateAgent.showUpdateDialog(MainActivity.this,updateResponse);
                    break;
                case UpdateResponse.HasNewVersion:
                        UpdateAgent.showUpdateDialog(MainActivity.this,updateResponse);
                    break;
                case UpdateResponse.NoUpdateVer:
                        Toast.makeText(MainActivity.this,"当前版本已经是最新",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    });
    UpdateAgent.checkUpdate(this);
}
```

至此已经集成完毕。

# 后续计划
* 上传更多的数据，方便服务器做更精细的灰度
* 支持用户自定义数据上传
* 下载断点续传
* 支持数据文件的更新
