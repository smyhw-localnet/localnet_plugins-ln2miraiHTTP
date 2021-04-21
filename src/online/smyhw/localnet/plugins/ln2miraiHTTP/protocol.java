package online.smyhw.localnet.plugins.ln2miraiHTTP;

import java.util.List;

import online.smyhw.localnet.LN;
import online.smyhw.localnet.message;
import online.smyhw.localnet.data.DataPack;
import online.smyhw.localnet.lib.Json;
import online.smyhw.localnet.lib.Exception.Json_Parse_Exception;
import online.smyhw.localnet.network.Client_sl;
import online.smyhw.localnet.network.protocol.StandardProtocol;

public class protocol implements StandardProtocol {
	long QQ;
	Client_sl upClient;
	public protocol(List input,Client_sl sy)
	{
		QQ = (long) input.get(0);
		this.upClient = sy;
	}

	/**
	 * 只实现message，其他打印给console就成
	 */
	@Override
	public void SendData(DataPack data) {
		if(data.getValue("type").equals("message"))
		{
			String msg = "["+LN.ID+"]:"+data.getValue("message");
			ForMirai.sendGroupMsg(QQ,msg);
			return;
		}
		if(data.getValue("type").equals("forward_message"))
		{
			String msg = "["+data.getValue("From")+"]:"+data.getValue("message");
			ForMirai.sendGroupMsg(QQ, msg);
			return;
		}
		message.warning("[ln2miraiHTTP][协议]:不支持的消息<"+data.getStr()+">(这不是错误，只是我咕咕咕了而已)");
		return;
		
	}
	
	public boolean SendTo_localnet(String msg)
	{
		DataPack dp;
		try 
		{
			msg = Json.Encoded(msg);
			dp = new DataPack("{\"type\":\"message\",\"message\":\""+msg+"\"}");
		} catch (Json_Parse_Exception e) {
			message.warning("[ln2miraiHTTP]:向localnet发送消息出错<"+msg+">", e);
			return false;
		}
		this.upClient.CLmsg(dp);
		return true;
	}
	
	/**
	 * 这会直接发送JSON，请检查源码以确保
	 * @param msg
	 * @return
	 */
	public boolean ALL_SendTo_localnet(String msg)
	{
		DataPack dp;
		try 
		{
			dp = new DataPack(msg);
		} catch (Json_Parse_Exception e) {
			message.warning("[ln2miraiHTTP]:向localnet发送真消息出错<"+msg+">", e);
			return false;
		}
		this.upClient.CLmsg(dp);
		return true;
	}
	

	@Override
	public void Disconnect() {
		message.warning("[ln2miraiHTTP]:虚拟客户端<"+lnp.ln2mCfg.get_String("ID_"+QQ,QQ+"")+">被localnet要求断开连接");
	}

}
