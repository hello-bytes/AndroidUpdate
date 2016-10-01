<?php


function GeneralSuccessJsonResult($result)
{
    return GeneralResult::createresult(0,"ok",$result)->toJson();
}

class GeneralResult {
    protected $errorCode = 0;
    protected $errorDesc;
    protected $result;

    static function create($code,$desc) {
        $result = new GeneralResult($code,$desc);
        return $result;
    }

    static function createresult($code,$desc,$result) {
        $result = new GeneralResult($code,$desc,$result);
        return $result;
    }

    function __construct($code = null, $desc = null,$result = null) {
        $this->errorCode = $code;
        $this->errorDesc = $desc;
        $this->result = $result;
    }

    function toJson()
    {
        //echo $errorcode . $errordesc;
        return json_encode(array
        (
        	'errorCode' => $this->errorCode,
        	'errorDesc' => $this->errorDesc,
        	//'data' => $this->result == null ? array() : $this->result,
        	'data' => $this->result,
        ));
    }
}
