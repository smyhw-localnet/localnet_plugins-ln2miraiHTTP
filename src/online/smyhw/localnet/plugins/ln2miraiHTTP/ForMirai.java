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
	 * ���ݸ����������ļ���ʼ����miraiHTTP������</br>
	 * ��Ҳ���������ӶϿ�ʱ�������ӣ����Կ����ظ�����
	 * @param cfg ���������ļ�
	 * @return �Ƿ��ʼ������
	 * @throws Exception 
	 */
	public static boolean start(config cfg) throws Exception
	{
		message.info("[ln2miraiHTTP]:��ʼ����MiraiHTTP������...");
		message.info("[ln2miraiHTTP]:��ȡ����...");
		MiraiHTTP_host = cfg.get_String("host", "127.0.0.1");
		MiraiHTTP_host = "http://"+MiraiHTTP_host+":"+cfg.get_String("port", "8080")+"/";
		String authKey = cfg.get_String("authKey", "δ�ҵ�");
		message.info("[ln2miraiHTTP]:������Ϣ<[authKey="+authKey+"],[apiURL="+MiraiHTTP_host+"]>");
		message.info("[ln2miraiHTTP]:��miraiע��...");
		String tmp1 = WebAPI.simplePost(MiraiHTTP_host+"auth", "{\"authKey\": \""+authKey+"\"}");
		Map tmp2 = parseRE(tmp1);
		session = (String) tmp2.get("session");
		message.info("[ln2miraiHTTP]:��ȡ��session<"+session+">");
		tmp1 = WebAPI.simplePost(MiraiHTTP_host+"verify","{\"sessionKey\": \""+session+"\",\"qq\": "+Long.parseLong(cfg.get_String("QQ", "δ�ҵ�"))+"}");
		message.info("[ln2miraiHTTP]:��֤session<"+tmp1+">");
		message.info("[ln2miraiHTTP]:��ʼ�����");
		//TODO
		return true;
	}
	
	/**
	 * ����Ⱥ��Ϣ
	 * @param qq ��Ҫ���͵�Ⱥ��
	 * @param msg ��Ҫ���͵���Ϣ
	 * @return �Ƿ��ͳɹ�
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
			message.warning("[ln2miraiHTTP]:������Ϣ��mirai����", e);
			try {ForMirai.start(lnp.ln2mCfg);} catch (Exception e1) {message.warning("[ln2miraiHTTP]:���³�ʼ��mirai����", e1);}
			return false;
		}
		return true;
	}
	
	/**
	 * ��ȡȺ�б�
	 * @return Ⱥ�б�
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
	 * ��ȡһ��Ⱥ��Ϣ
	 * @return Ⱥ��Ϣ��û��ʱ�᷵������
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
			message.warning("[ln2miraiHTTP]:����,miraiHTTP�����˷�0״̬��"+(Integer)remap.get("code")+",�����˵�������˴���![��ʼ��������]");
			try 
			{
				ForMirai.start(lnp.ln2mCfg);
			} 
			catch (Exception e) 
			{
				message.warning("����������mirai����", e);
			}
		}
		return remap;
	}
}
