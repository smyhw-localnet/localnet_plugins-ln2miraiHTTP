package online.smyhw.localnet.plugins.ln2miraiHTTP;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import online.smyhw.localnet.message;
import online.smyhw.localnet.data.config;
import online.smyhw.localnet.lib.WebAPI;

import com.alibaba.fastjson.JSON;

public class ForMirai 
{
	public static String MiraiHTTP_host;
	public static String session;
	/**
	 * 根据给定的配置文件初始化和miraiHTTP的连接</br>
	 * 这也被用于连接断开时重新连接，所以可以重复调用
	 * @param cfg 给定配置文件
	 * @return 是否初始化正常
	 * @throws Exception 
	 */
	public static boolean start(config cfg) throws Exception
	{
		message.info("[ln2miraiHTTP]:初始化和MiraiHTTP的连接...");
		message.info("[ln2miraiHTTP]:读取配置...");
		MiraiHTTP_host = cfg.get_String("host", "127.0.0.1");
		MiraiHTTP_host = "http://"+MiraiHTTP_host+":"+cfg.get_String("port", "8080")+"/";
		String authKey = cfg.get_String("authKey", "未找到");
		message.info("[ln2miraiHTTP]:配置信息<[authKey="+authKey+"],[apiURL="+MiraiHTTP_host+"]>");
		message.info("[ln2miraiHTTP]:向mirai注册...");
		String tmp1 = WebAPI.simplePost(MiraiHTTP_host+"auth", "{\"authKey\": \""+authKey+"\"}");
		Map tmp2 = parseRE(tmp1);
		session = (String) tmp2.get("session");
		message.info("[ln2miraiHTTP]:获取到session<"+session+">");
		tmp1 = WebAPI.simplePost(MiraiHTTP_host+"verify","{\"sessionKey\": \""+session+"\",\"qq\": "+Long.parseLong(cfg.get_String("QQ", "未找到"))+"}");
		message.info("[ln2miraiHTTP]:认证session<"+tmp1+">");
		message.info("[ln2miraiHTTP]:初始化完成");
		//TODO
		return true;
	}
	
	/**
	 * 发送群消息
	 * @param qq 需要发送的群号
	 * @param msg 需要发送的消息
	 * @return 是否发送成功
	 */
	public static boolean sendGroupMsg(long qq,String msg)
	{
		String smsg = "{\"sessionKey\": \""+session+"\",\"target\": "+qq+",\"messageChain\":[{ \"type\": \"Plain\", \"text\": \""+msg+"\" }]}";
		try
		{
			WebAPI.simplePost(MiraiHTTP_host+"sendGroupMessage", smsg);
		}
		catch (Exception e) 
		{
			message.warning("[ln2miraiHTTP]:发送消息至mirai出错", e);
			try {ForMirai.start(lnp.ln2mCfg);} catch (Exception e1) {message.warning("[ln2miraiHTTP]:重新初始化mirai出错", e1);}
			return false;
		}
		return true;
	}
	
	/**
	 * 获取群列表
	 * @return 群列表
	 * @throws Exception 
	 */
	public static List<Long> getGroupList() throws Exception
	{
		List<Long> re = new ArrayList<Long>();
		String tmp1 = WebAPI.simpleGet(MiraiHTTP_host+"groupList?sessionKey="+session);
		List<Map> tmp2 = JSON.parseObject(tmp1, List.class);
		for(int num=0;num<tmp2.size();num++)
		{
			long id = Long.valueOf(tmp2.get(num).get("id")+"");
			re.add(id);
		}
		return re;
	}
	
	/**
	 * 获取一条群消息
	 * @return 群消息，没有时会返回阻塞
	 * @throws Exception 
	 */
	public static Map getGroupMsg() throws Exception
	{
		while(true)
		{
			String tmp1 = WebAPI.simpleGet(MiraiHTTP_host+"fetchMessage?sessionKey="+session+"&count=1");
			Map tmp2 = parseRE(tmp1);
			List tmp4 = (List)tmp2.get("data");
			if(tmp4.size()==0) 
			{
				Thread.sleep(1000);
				continue;
			}
			Map tmp3 = (Map)(tmp4.get(0));
			if(!tmp3.get("type").equals("GroupMessage"))
			{
				continue;
			}
			return tmp3;
		}

	}
	
	
	public static Map<String,String> parseRE(String re)
	{
		Map remap = JSON.parseObject(re, Map.class);
		if( (Integer)remap.get("code")  != 0 )
		{
			message.warning("[ln2miraiHTTP]:警告,miraiHTTP返回了非0状态码"+(Integer)remap.get("code")+",这可能说明发生了错误![开始尝试重连]");
			try 
			{
				ForMirai.start(lnp.ln2mCfg);
			} 
			catch (Exception e) 
			{
				message.warning("重新连接至mirai出错", e);
			}
		}
		return remap;
	}
}
