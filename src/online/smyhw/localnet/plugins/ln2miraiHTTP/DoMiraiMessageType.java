package online.smyhw.localnet.plugins.ln2miraiHTTP;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import online.smyhw.localnet.message;

public class DoMiraiMessageType 
{
	public static String doMessage(List input)
	{
		String re="";
		for(int i=0;i<input.size();i++)
		{
			Map oneMap = (Map) input.get(i);
			re = re + MtoString(oneMap);
		}
		return re;
	}
	
	public static String MtoString(Map input)
	{
		String re = "";
		switch((String)input.get("type"))
		{
		case"Source":
			re = do_Source(input);
			break;
		case"Quote":
			re = do_Quote(input);
			break;
		case"At":
			re = do_At(input);
			break;
		case"AtAll":
			re = do_AtAll(input);
			break;
		case"Face":
			re = do_Face(input);
			break;
		case"Plain":
			re = do_Plain(input);
			break;
		case"Image":
			re = do_Image(input);
			break;
		case"FlashImage":
			re = do_FlashImage(input);
			break;
		case"Voice":
			re = do_Voice(input);
			break;
		case"Xml":
			re = do_Xml(input);
			break;
		case"Json":
			re = do_Json(input);
			break;
		case"App":
			re = do_App(input);
			break;
		case"Poke":
			re = do_Poke(input);
			break;
		default:
			message.warning("[ln2miraiHTTP]:mirai������δ֪���͵���Ϣ<"+JSON.toJSONString(input)+">");
			return "[����|δ֪���͵���Ϣ]";
		}
		return re;
	}
	
	public static String do_Source(Map input)
	{//�����Ϣ���ͽ�������ϢID����Ϣ����ʱ��
		return "";
	}
	
	public static String do_Quote(Map input)
	{
		//TODO ��ѯ���ظ��˵�����
		return "�ظ�:";
	}
	
	public static String do_At(Map input)
	{
		return (String) input.get("dispaly");
	}
	
	public static String do_AtAll(Map input)
	{
		return "@����èè";
	}
	
	public static String do_Face(Map input)
	{
		return "[QQ����|"+(String)input.get("faceID")+"|"+(String)input.get("name")+"]";
	}
	
	public static String do_Plain(Map input)
	{
		return (String) input.get("text");
	}
	
	public static String do_Image(Map input)
	{
		return "[ͼƬ]";
	}
	
	public static String do_FlashImage(Map input)
	{
		return "[����]";
	}
	
	public static String do_Voice(Map input)
	{
		return "[����]";
	}
	
	public static String do_Xml(Map input)
	{
		return "[Xml��ʽ�ı�]";
	}
	
	public static String do_Json(Map input)
	{
		return "[Json��ʽ�ı�]";
	}
	
	public static String do_App(Map input)
	{
		return "[App����]";
	}
	
	public static String do_Poke(Map input)
	{
		switch((String)input.get("name"))
		{
		case "Poke":
			return "[��һ��]";
		case "ShowLove":
			return "[[��һ��|����]";
		case "Like":
			return "[��һ��|����]";
		case "Heartbroken":
			return "[��һ��|����]";
		case "SixSixSix":
			return "[��һ��|666]";
		case "FangDaZhao":
			return "[��һ��|�Ŵ���]";
		default:
			return "[δ֪��һ������|"+(String)input.get("name")+"]";
		}
	}
	
}
