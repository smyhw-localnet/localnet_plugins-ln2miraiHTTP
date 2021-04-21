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
	 * ֻʵ��message��������ӡ��console�ͳ�
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
		message.warning("[ln2miraiHTTP][Э��]:��֧�ֵ���Ϣ<"+data.getStr()+">(�ⲻ�Ǵ���ֻ���ҹ������˶���)");
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
			message.warning("[ln2miraiHTTP]:��localnet������Ϣ����<"+msg+">", e);
			return false;
		}
		this.upClient.CLmsg(dp);
		return true;
	}
	
	/**
	 * ���ֱ�ӷ���JSON������Դ����ȷ��
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
			message.warning("[ln2miraiHTTP]:��localnet��������Ϣ����<"+msg+">", e);
			return false;
		}
		this.upClient.CLmsg(dp);
		return true;
	}
	

	@Override
	public void Disconnect() {
		message.warning("[ln2miraiHTTP]:����ͻ���<"+lnp.ln2mCfg.get_String("ID_"+QQ,QQ+"")+">��localnetҪ��Ͽ�����");
	}

}
