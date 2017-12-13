package com.aaden.pay.api.comm.model;

import java.math.BigDecimal;
import java.util.Date;

import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.CardProp;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;
import com.aaden.pay.api.comm.enums.SendStatus;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.core.eumus.YesOrNo;
import com.aaden.pay.core.model.BaseModel;

/**
 * @Description 支付交易记录
 * @author aaden
 * @date 2017年12月20日
 */
public class ThirdPayRecord extends BaseModel {

	private static final long serialVersionUID = -4942015778367297522L;

	private int id; //

	private String tradeId; // 交易ID

	private String userId; // 用户ID

	private String serialnumber;// 第三方支付交易流水号 用于交易查询

	private String thirdnumber;// 第三方支付订单号,支付公司返回

	private String cardNo; // 银行卡号

	private String realName; // 真实姓名

	private BankType bankType;// 开户行代号

	private BigDecimal orderAmount; // 总金额

	private BigDecimal actAmount;// 实际发生金额

	private String orderCode; // 订单号

	private PayType payType; // 支付类型

	private PayChannel payChannel;// 支付渠道

	private Date sendTime; // 发送时间

	private Date successTime; // 支付成功时间,即完成时间

	private Date settleTime;// 支付公司清算日期

	private SendStatus sendStatus;// 发送状态

	private int sendCount; // 发送次数(暂时用于查询次数)

	private TradeStatus tradeStatus;// 交易处理状态

	private String payCode; // 支付响应代号

	private String payMessage; // 支付响应消息

	private Date updateTime; // 最后更新时间

	private String callbackStatus;// 业务是否已经回调

	private BigDecimal costs;// 支付成本,第三方支付公司收取的手续费

	private CardProp cardProp;// 银行卡属性

	// 查询字段
	private Date sendStart;
	private Date sendEnd;

	public ThirdPayRecord() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSerialnumber() {
		return serialnumber;
	}

	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
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

	public String getBankTypeCn() {
		return bankType == null ? "" : bankType.getCnName();
	}

	public void setBankType(BankType bankType) {
		this.bankType = bankType;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	public BigDecimal getActAmount() {
		return actAmount;
	}

	public void setActAmount(BigDecimal actAmount) {
		this.actAmount = actAmount;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public PayType getPayType() {
		return payType;
	}

	public void setPayType(PayType payType) {
		this.payType = payType;
	}

	public PayChannel getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(PayChannel payChannel) {
		this.payChannel = payChannel;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public SendStatus getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(SendStatus sendStatus) {
		this.sendStatus = sendStatus;
	}

	public int getSendCount() {
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getPayMessage() {
		return payMessage;
	}

	public void setPayMessage(String payMessage) {
		this.payMessage = payMessage;
	}

	public TradeStatus getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(TradeStatus tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public BigDecimal getCosts() {
		return costs;
	}

	public void setCosts(BigDecimal costs) {
		this.costs = costs;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCallbackStatus() {
		return callbackStatus;
	}

	public void setCallbackStatus(String callbackStatus) {
		this.callbackStatus = callbackStatus;
	}

	public void initPrimaryKey() {
		this.setTradeId(randomUUID());
	}

	public Date getSuccessTime() {
		return successTime;
	}

	public void setSuccessTime(Date successTime) {
		this.successTime = successTime;
	}

	public Date getSettleTime() {
		return settleTime;
	}

	public void setSettleTime(Date settleTime) {
		this.settleTime = settleTime;
	}

	public Date getSendStart() {
		return sendStart;
	}

	public void setSendStart(Date sendStart) {
		this.sendStart = sendStart;
	}

	public Date getSendEnd() {
		return sendEnd;
	}

	public void setSendEnd(Date sendEnd) {
		this.sendEnd = sendEnd;
	}

	public String getThirdnumber() {
		return thirdnumber;
	}

	public void setThirdnumber(String thirdnumber) {
		this.thirdnumber = thirdnumber;
	}

	public CardProp getCardProp() {
		return cardProp;
	}

	public void setCardProp(CardProp cardProp) {
		this.cardProp = cardProp;
	}

	/**
	 * 支付渠道中文
	 * 
	 * @return
	 */
	public String getPayChannelCn() {
		return this.payChannel == null ? "" : this.payChannel.getCnName();
	}

	/**
	 * 支付类型中文
	 * 
	 * @return
	 */
	public String getPayTypeCn() {
		return this.payType == null ? "" : this.payType.getName();
	}

	/**
	 * 发送状态中文
	 * 
	 * @return
	 */
	public String getSendStatusCn() {
		return this.sendStatus == null ? "" : this.sendStatus.getCnName();
	}

	/**
	 * 交易状态中文
	 * 
	 * @return
	 */
	public String getTradeStatusCn() {
		return this.tradeStatus == null ? "" : this.tradeStatus.getName();
	}

	/**
	 * 回调状态中文
	 * 
	 * @return
	 */
	public String getCallbackStatusCn() {
		return YesOrNo.getCnNameByCode(this.callbackStatus);
	}

	/**
	 * 是否是代付
	 */
	public boolean isPayOut() {
		return PayType.PAYOUT == (this.payType);
	}

	/**
	 * 是否可以对账
	 */
	public boolean isCheckable() {
		return !this.isSuccess();
	}

	/**
	 * 是否可以回调
	 */
	public boolean isCallbackable() {
		if (YesOrNo.NO.getCode().equals(this.callbackStatus)) {
			return !this.isNotSure();
		}
		return false;
	}

	/**
	 * 是否为失败的支付
	 */
	public boolean isFail() {
		if (TradeStatus.FAILURE == (this.tradeStatus)) {
			return true;
		}
		if (TradeStatus.OVERDUE == (this.tradeStatus)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否为成功的支付
	 */
	public boolean isSuccess() {
		return TradeStatus.SUCCEED == (this.tradeStatus);
	}

	/**
	 * 是否为处理中的支付
	 */
	public boolean isNotSure() {
		return !this.isFail() && !this.isSuccess();
	}

}
