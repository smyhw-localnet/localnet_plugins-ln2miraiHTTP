package online.smyhw.localnet.plugins.ln2miraiHTTP;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import online.smyhw.localnet.message;
import online.smyhw.localnet.command.cmdManager;
import online.smyhw.localnet.data.DataManager;
import online.smyhw.localnet.data.DataPack;
import online.smyhw.localnet.data.config;
import online.smyhw.localnet.event.Chat_Event;
import online.smyhw.localnet.event.EventManager;
import online.smyhw.localnet.lib.Json;
import online.smyhw.localnet.lib.Exception.Json_Parse_Exception;
import online.smyhw.localnet.network.Client_sl;

public class lnp 
{
	public static config ln2mCfg;
	public static Map<Long,Client_sl> Client_slMap = new HashMap<Long,Client_sl>();
	public static void plugin_loaded()
	{
		message.info("ln2miraiHTTP�������...");
		try 
		{
			//ע��ָ��<exampleCMD>�����ָ�ִ��ʱ�����÷���<cmd>(���·������������)
//			cmdManager.add_cmd("exampleCMD", lnp.class.getMethod("cmd", new Class[]{Client_sl.class,String.class}));
			//ע���¼�������
			//��������¼��ķ�����<listener>(���·������������)��������¼���<ChatINFO_Event>
			//ע�⣬ChatINFO����������¼������ƣ�������online.smyhw.event�µ��¼����嶨���п���
			//�����¼�������online.smyhw.event��ͷ����
			//���<ChatINFO_Event>�¼��������пͻ��˳��Է���������Ϣʱ�ᱻ����
//			EventManager.AddListener("ChatINFO", lnp.class.getMethod("listener", new Class[] {ChatINFO_Event.class}));
		} 
		catch (Exception e) 
		{
			message.warning("ln2miraiHTTP������ش���!",e);
		}
		message.info("[ln2miraiHTTP]��ȡ�����ļ�[./configs/ln2mirai.config]...");
		try 
		{
			boolean tmp1= DataManager.makeNewConfigFile("./configs/ln2mirai.config", "/online/smyhw/localnet/plugins/ln2miraiHTTP/exampleCfg",lnp.class);
			if (tmp1)
			{
				message.info("[ln2miraiHTTP]�������ļ������ɹ�����༭������localnet...");
				return;
			}
		}
		catch (Exception e1) 
		{
			message.warning("[ln2miraiHTTP]�������ļ�����ʧ�ܣ�����...",e1);
			return;
		}
		ln2mCfg = DataManager.LoadConfig("./configs/ln2mirai.config");
		if(ln2mCfg.get_String("host", null) == null)
		{
			message.warning("[ln2miraiHTTP]:û���ҵ�������Ŀ<host>,���������ļ������Ƿ���ȷ��ln2miraiHTTP�����������...");
			return;
		}
		List<Long> GroupList;
		try 
		{
			ForMirai.start(ln2mCfg);
			GroupList=ForMirai.getGroupList();
		}
		catch (Exception e) 
		{
			message.warning("[ln2miraiHTTP]:��ʼ����mirai�������쳣�����飬�����������...",e);
			return;
		}
		message.info("[ln2miraiHTTP]:��ȡ��Ⱥ�б�<"+GroupList.toString()+">");
		for(int num=0;num<GroupList.size();num++)
		{
			List tmp1 = new ArrayList();
			tmp1.add(GroupList.get(num));
			Client_sl cs = new Client_sl("online.smyhw.localnet.plugins.ln2miraiHTTP.protocol", tmp1);
			try {cs.CLmsg(new DataPack("{\"type\":\"auth\",\"ID\":\""+ lnp.ln2mCfg.get_String("ID_"+GroupList.get(num),GroupList.get(num)+"")+"\"}"));} catch (Json_Parse_Exception e) {e.printStackTrace();}//�ⲻ�ó����쳣
			Client_slMap.put(GroupList.get(num), cs);
		}
		new getMsgThread();
	}
	
	/**
	 * <exampleCMD>��ִ��ʱ�����������������
	 * 
	 * @param User ����ʹ������Ŀͻ���
	 * @param cmd ʹ�����ָ��ʱ����������ַ���
	 * ���磺/exampleCMD arg1 arg2 arg3 </br>
	 * ��ô��cmd��ֵ����<exampleCMD arg1 arg2 arg3>
	 */
	public static void cmd(Client_sl User,String cmd)
	{
		User.sendMsg("��ʹ����ʾ��ָ��<"+cmd+">");
	}
	
	
	/**
	 * ���¼�����ʱ�����������������
	 * @param dd �������¼�
	 */
	public static void listener(ChatINFO_Event dd)
	{
		//���ֵ����Ϊtrue�����ݲ�ͬ���¼����в�ͬ�ķ�Ӧ
		//��ͻ��˶Ͽ������¼��ȣ����ֵ���ܾ�����Ч��/���ܱ����õ�
		//���<ChatINFO_Event>�¼��У��������ֵΪtrue����������Ϣ���ᱻ���͵�Ŀ��ͻ���
		//dd.Cancel=true;
		Client_sl ComeFromClient = dd.From_User;//�����Ϣ��˭���͵�
		Client_sl ToClient = dd.To_User;//�����Ϣ�Ƿ���˭��
		String msg = dd.msg;//��Ϣ������
		
		message.info("[���Բ��]�ͻ���<"+ComeFromClient.remoteID+">��ͻ���<"+ToClient.remoteID+">������һ����Ϣ<"+msg+">");
	}
}

class getMsgThread extends Thread
{
	public getMsgThread()
	{
		this.start();
	}
	public void run()
	{
		Map re ;
		while(true)
		{
			try 
			{
				re = ForMirai.getGroupMsg();
			}
			catch (Exception e) 
			{
				message.warning("[ln2miraiHTTP]:����Ⱥ��Ϣ����", e);
				continue;
			}
			Map tmp4 = (Map) re.get("sender");
			String SenderName = (String) tmp4.get("memberName");
			String Message = DoMiraiMessageType.doMessage((List) re.get("messageChain"));
			Map tmp5 = (Map) tmp4.get("group");
			Long ID = Long.valueOf(tmp5.get("id")+"");
			Client_sl cs = lnp.Client_slMap.get(ID);
			//���￪ʼ��������Ϣ����
			try 
			{
				String headKey = Message.substring(0, 1);//�������쳣���������̹߳ҵ�
				switch(headKey)
				{
				case "#":
					if(!(""+tmp4.get("id")).equals(lnp.ln2mCfg.get_String("admin", "3440134586")))
					{
						ForMirai.sendGroupMsg(ID, "[localnetOS]:Ȩ�޲���");
						continue;
					}
					((protocol)cs.protocolClass).ALL_SendTo_localnet("{\"type\":\"command\",\"CmdText\":\""+Json.Encoded(Message.substring(1))+"\"}");
					continue;
				}
			}catch(IndexOutOfBoundsException  e) {}
			
			String FinMessage ="[" +SenderName+"]:"+Message;
			

			((protocol)cs.protocolClass).SendTo_localnet(FinMessage);
		}
	}
}
