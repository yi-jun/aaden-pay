package com.aaden.pay.service.biz.vo;

/**
 *  @Description 第三方支付响应
 *  @author aaden
 *  @date 2017年12月26日
 */
public class ThirdBankResponse {
	private boolean success;

	private String returnCode; // 响应代码

	private String returnMsg; // 验证结果

	private String returnData; // 响应报文

	private String agreeNo;// 签约协议号

	private ThirdBankResponse() {

	}

	public static ThirdBankResponse getSuccessInstance(String returnCode, String returnMsg, String returnData) {
		ThirdBankResponse resp = new ThirdBankResponse();
		resp.success = true;
		resp.returnCode = returnCode;
		resp.returnMsg = returnMsg;
		resp.returnData = returnData;
		return resp;
	}

	public static ThirdBankResponse getFailInstance(String returnCode, String returnMsg, String returnData) {
		ThirdBankResponse resp = new ThirdBankResponse();
		resp.success = false;
		resp.returnCode = returnCode;
		resp.returnMsg = returnMsg;
		resp.returnData = returnData;
		return resp;
	}

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public String getReturnData() {
		return returnData;
	}

	public void setReturnData(String returnData) {
		this.returnData = returnData;
	}

	public String getAgreeNo() {
		return agreeNo;
	}

	public void setAgreeNo(String agreeNo) {
		this.agreeNo = agreeNo;
	}

}
