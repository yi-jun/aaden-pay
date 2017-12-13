package com.aaden.pay.api.biz.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.CardProp;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;

/**
 *  @Description 支付请求数据
 *  @author aaden
 *  @date 2017年12月25日
 */
public class PayRequest implements Serializable {

	private static final long serialVersionUID = -8557764868964112564L;

	// 必填数据
	private MustData must = new MustData();

	// 充值选填数据
	private RechargeData recharge = new RechargeData();

	// 提现选填数据
	private CashData cash = new CashData();

	// 系统使用参数, 不需传递
	private SysData sys = new SysData();

	public MustData getMust() {
		return must;
	}

	public RechargeData getRecharge() {
		return recharge;
	}

	public CashData getCash() {
		return cash;
	}

	public SysData getSys() {
		return sys;
	}

	/**
	 * 必须参数
	 */
	public class MustData implements Serializable{
		private static final long serialVersionUID = -1637895572493974205L;

		private MustData() {
		}

		private String userId; // 用户ID
		private String userLoginName;// 登录名,冗余
		private CardProp cardProp = CardProp.person; // 账号属性,默认为个人
		private String cardNo; // 银行卡号
		private String realName; // 开户人姓名
		private BankType bankType;// 银行类型
		private BigDecimal amount; // 本次支付金额
		private String orderCode; // 系统内部订单号,唯一标识一笔订单,用于重复支付判断

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getUserLoginName() {
			return userLoginName;
		}

		public void setUserLoginName(String userLoginName) {
			this.userLoginName = userLoginName;
		}

		public CardProp getCardProp() {
			return cardProp;
		}

		public void setCardProp(CardProp cardProp) {
			this.cardProp = cardProp;
		}

		public String getCardNo() {
			return cardNo;
		}

		public void setCardNo(String cardNo) {
			this.cardNo = cardNo;
		}

		public String getRealName() {
			return realName;
		}

		public void setRealName(String realName) {
			this.realName = realName;
		}

		public BankType getBankType() {
			return bankType;
		}

		public void setBankType(BankType bankType) {
			this.bankType = bankType;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

		public String getOrderCode() {
			return orderCode;
		}

		public void setOrderCode(String orderCode) {
			this.orderCode = orderCode;
		}

	}

	/**
	 * 代付选填参数
	 */
	public class CashData implements Serializable{
		private static final long serialVersionUID = -5088931332493583471L;

		private CashData() {
		}

		private String payRemark; // 代付银行流水备注(部分银行支持)
		private String branchName;// 开户行支行名称
		private String bankCity;// 开户行城市的编码
		private String bankProv;// 开户行省份编码
		private String mobile;// 预留手机号码(宝付代付选填)
		private String idno;// 身份证号码(宝付代付选填)
		private PayChannel payChannel; // 支付渠道
		/**
		 * 判断重复支付依据为: (当前支付成功金额+处理中的金额+amount) <= orderTotalAmount,则可以支付
		 */
		private BigDecimal orderTotalAmount;// 该订单总金额,

		public BigDecimal getOrderTotalAmount() {
			return orderTotalAmount;
		}

		public void setOrderTotalAmount(BigDecimal orderTotalAmount) {
			this.orderTotalAmount = orderTotalAmount;
		}

		public String getPayRemark() {
			return payRemark;
		}

		public void setPayRemark(String payRemark) {
			this.payRemark = payRemark;
		}

		public String getBranchName() {
			return branchName;
		}

		public void setBranchName(String branchName) {
			this.branchName = branchName;
		}

		public String getBankCity() {
			return bankCity;
		}

		public void setBankCity(String bankCity) {
			this.bankCity = bankCity;
		}

		public String getBankProv() {
			return bankProv;
		}

		public void setBankProv(String bankProv) {
			this.bankProv = bankProv;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getIdno() {
			return idno;
		}

		public void setIdno(String idno) {
			this.idno = idno;
		}

		public PayChannel getPayChannel() {
			return payChannel;
		}

		public void setPayChannel(PayChannel payChannel) {
			this.payChannel = payChannel;
		}

	}

	/**
	 * 充值选填参数
	 */
	public class RechargeData implements Serializable{
		private static final long serialVersionUID = 3823491825293927785L;

		private RechargeData() {
		}

		private String agreeNo;// 认证支付签约协议号
		private String idNo;// 身份证
		private String idNoType;// 身份证类型
		private String token;// 充值验证码时,第三方预留的token,确认充值时传递给第三方支付公司
		private String validCode;// 验证码
		private String mobile; // 预留手机号码
		private String clientIp;// 用户请求ip,(宝付支付充值时必填)
		private PayChannel payChannel; // 支付渠道(可指定,未指定由系统路由自动判断)

		public String getAgreeNo() {
			return agreeNo;
		}

		public void setAgreeNo(String agreeNo) {
			this.agreeNo = agreeNo;
		}

		public String getIdNo() {
			return idNo;
		}

		public void setIdNo(String idNo) {
			this.idNo = idNo;
		}

		public String getIdNoType() {
			return idNoType;
		}

		public void setIdNoType(String idNoType) {
			this.idNoType = idNoType;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getValidCode() {
			return validCode;
		}

		public void setValidCode(String validCode) {
			this.validCode = validCode;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getClientIp() {
			return clientIp;
		}

		public void setClientIp(String clientIp) {
			this.clientIp = clientIp;
		}

		public PayChannel getPayChannel() {
			return payChannel;
		}

		public void setPayChannel(PayChannel payChannel) {
			this.payChannel = payChannel;
		}

	}

	/**
	 * 系统参数
	 */
	public class SysData implements Serializable{
		private static final long serialVersionUID = 7052753973136720271L;
		private PayChannel payChannel; // 支付渠道
		private PayType payType;// 支付方式
		private String rechargeToken;// 充值验证码的token

		private SysData() {
		}

		public PayChannel getPayChannel() {
			return payChannel;
		}

		public void setPayChannel(PayChannel payChannel) {
			this.payChannel = payChannel;
		}

		public PayType getPayType() {
			return payType;
		}

		public void setPayType(PayType payType) {
			this.payType = payType;
		}

		public String getRechargeToken() {
			return rechargeToken;
		}

		public void setRechargeToken(String rechargeToken) {
			this.rechargeToken = rechargeToken;
		}

	}

}
