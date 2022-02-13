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
		message.info("ln2miraiHTTP插件加载...");
		try 
		{
			//注册指令<exampleCMD>，这个指令被执行时将调用方法<cmd>(在下方定义这个方法)
//			cmdManager.add_cmd("exampleCMD", lnp.class.getMethod("cmd", new Class[]{Client_sl.class,String.class}));
			//注册事件监听器
			//处理这个事件的方法是<listener>(在下方定义这个方法)，处理的事件是<ChatINFO_Event>
			//注意，ChatINFO这个参数是事件的名称，可以在online.smyhw.event下的事件具体定义中看到
			//所有事件可以在online.smyhw.event里头看到
			//这个<ChatINFO_Event>事件，在所有客户端尝试发送聊天消息时会被触发
//			EventManager.AddListener("ChatINFO", lnp.class.getMethod("listener", new Class[] {ChatINFO_Event.class}));
		} 
		catch (Exception e) 
		{
			message.warning("ln2miraiHTTP插件加载错误!",e);
		}
		message.info("[ln2miraiHTTP]读取配置文件[./configs/ln2mirai.config]...");
		try 
		{
			boolean tmp1= DataManager.makeNewConfigFile("./configs/ln2mirai.config", "/online/smyhw/localnet/plugins/ln2miraiHTTP/exampleCfg",lnp.class);
			if (tmp1)
			{
				message.info("[ln2miraiHTTP]新配置文件创建成功，请编辑后重启localnet...");
				return;
			}
		}
		catch (Exception e1) 
		{
			message.warning("[ln2miraiHTTP]新配置文件创建失败，请检查...",e1);
			return;
		}
		ln2mCfg = DataManager.LoadConfig("./configs/ln2mirai.config");
		if(ln2mCfg.get_String("host", null) == null)
		{
			message.warning("[ln2miraiHTTP]:没有找到配置项目<host>,请检查配置文件内容是否正确，ln2miraiHTTP不会继续加载...");
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
			message.warning("[ln2miraiHTTP]:初始化和mirai的连接异常，请检查，不会继续加载...",e);
			return;
		}
		message.info("[ln2miraiHTTP]:读取到群列表<"+GroupList.toString()+">");
		for(int num=0;num<GroupList.size();num++)
		{
			List tmp1 = new ArrayList();
			tmp1.add(GroupList.get(num));
			Client_sl cs = new Client_sl("online.smyhw.localnet.plugins.ln2miraiHTTP.protocol", tmp1);
			try {cs.CLmsg(new DataPack("{\"type\":\"auth\",\"ID\":\""+ lnp.ln2mCfg.get_String("ID_"+GroupList.get(num),GroupList.get(num)+"")+"\"}"));} catch (Json_Parse_Exception e) {e.printStackTrace();}//这不该出现异常
			Client_slMap.put(GroupList.get(num), cs);
		}
		new getMsgThread();
	}
	
	/**
	 * <exampleCMD>被执行时，这个方法将被调用
	 * 
	 * @param User 这是使用这个的客户端
	 * @param cmd 使用这个指令时传入的完整字符串
	 * 例如：/exampleCMD arg1 arg2 arg3 </br>
	 * 那么，cmd的值就是<exampleCMD arg1 arg2 arg3>
	 */
	public static void cmd(Client_sl User,String cmd)
	{
		User.sendMsg("你使用了示例指令<"+cmd+">");
	}
	
	
	/**
	 * 当事件发送时，这个方法将被调用
	 * @param dd 发生的事件
	 */
	public static void listener(ChatINFO_Event dd)
	{
		//这个值设置为true，根据不同的事件会有不同的反应
		//像客户端断开连接事件等，这个值可能就是无效的/不能被设置的
		//这个<ChatINFO_Event>事件中，设置这个值为true，则这则消息不会被发送到目标客户端
		//dd.Cancel=true;
		Client_sl ComeFromClient = dd.From_User;//这个消息是谁发送的
		Client_sl ToClient = dd.To_User;//这个消息是发给谁的
		String msg = dd.msg;//消息的内容
		
		message.info("[测试插件]客户端<"+ComeFromClient.remoteID+">向客户端<"+ToClient.remoteID+">发送了一则消息<"+msg+">");
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
				message.warning("[ln2miraiHTTP]:接收群消息出错", e);
				continue;
			}
			Map tmp4 = (Map) re.get("sender");
			String SenderName = (String) tmp4.get("memberName");
			String Message = DoMiraiMessageType.doMessage((List) re.get("messageChain"));
			Map tmp5 = (Map) tmp4.get("group");
			Long ID = Long.valueOf(tmp5.get("id")+"");
			Client_sl cs = lnp.Client_slMap.get(ID);
			//这里开始，特殊消息处理
			try 
			{
				String headKey = Message.substring(0, 1);//这里抛异常会让整个线程挂掉
				switch(headKey)
				{
				case "#":
					if(!(""+tmp4.get("id")).equals(lnp.ln2mCfg.get_String("admin", "3440134586")))
					{
						ForMirai.sendGroupMsg(ID, "[localnetOS]:权限不足");
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
