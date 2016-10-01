<?php


require_once "resulthelper.php";
function getQueryString($key){
		if(array_key_exists($key, $_GET)){
			return $_GET["{$key}"];
		}
		if(array_key_exists($key, $_POST)){
			return $_POST["{$key}"];
		}
		return "";
}

$product = getQueryString("product","");
if(strcasecmp($product,"test") != 0){
  exit("this test server only support productname : test");
}

$ver = getQueryString("ver","0");
if(intval($ver) <= 0){
  exit("you must tell me your version");
}

echo GeneralSuccessJsonResult(
    array(
        "updateType" => 1,
        "updateLog" => "just for test",
        "version" => 123,
        "fileSize" => 10982,
        "url" => "http://codingsky.oss-cn-hangzhou.aliyuncs.com/cdn/soft/tbs_1.0.1030.apk",
    )
);
