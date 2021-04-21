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
			message.warning("[ln2miraiHTTP]:mirai传回了未知类型的消息<"+JSON.toJSONString(input)+">");
			return "[错误|未知类型的消息]";
		}
		return re;
	}
	
	public static String do_Source(Map input)
	{//这个消息类型仅包含消息ID，消息发送时间
		return "";
	}
	
	public static String do_Quote(Map input)
	{
		//TODO 查询被回复人的名称
		return "回复:";
	}
	
	public static String do_At(Map input)
	{
		return (String) input.get("dispaly");
	}
	
	public static String do_AtAll(Map input)
	{
		return "@所有猫猫";
	}
	
	public static String do_Face(Map input)
	{
		return "[QQ表情|"+(String)input.get("faceID")+"|"+(String)input.get("name")+"]";
	}
	
	public static String do_Plain(Map input)
	{
		return (String) input.get("text");
	}
	
	public static String do_Image(Map input)
	{
		return "[图片]";
	}
	
	public static String do_FlashImage(Map input)
	{
		return "[闪照]";
	}
	
	public static String do_Voice(Map input)
	{
		return "[语音]";
	}
	
	public static String do_Xml(Map input)
	{
		return "[Xml格式文本]";
	}
	
	public static String do_Json(Map input)
	{
		return "[Json格式文本]";
	}
	
	public static String do_App(Map input)
	{
		return "[App内容]";
	}
	
	public static String do_Poke(Map input)
	{
		switch((String)input.get("name"))
		{
		case "Poke":
			return "[戳一戳]";
		case "ShowLove":
			return "[[戳一戳|比心]";
		case "Like":
			return "[戳一戳|点赞]";
		case "Heartbroken":
			return "[戳一戳|心碎]";
		case "SixSixSix":
			return "[戳一戳|666]";
		case "FangDaZhao":
			return "[戳一戳|放大招]";
		default:
			return "[未知戳一戳类型|"+(String)input.get("name")+"]";
		}
	}
	
}
