package com.expample.privacy;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class ReadFile {
	private File file = null;
	private static InputStream in = null;
	private static int index = 0 ;
	private static long total_index = 0 ;
	private String link_type = null;

	public List<PacketModel> readFiletoArraylist(String fileName){
		file = new File(fileName);
		long filesize = file.length() ;
		total_index = 0 ;
		link_type = null ;
		List<PacketModel> packets = new ArrayList<PacketModel>() ; 
		try {
			in = new FileInputStream(file) ;
			readByteToString(20) ;							//跳过无用字节
			link_type = readByteToString(1) ;				//获取type
			readByteToString(3) ;							//跳过无用字节
			while(true){
				index = 0 ;
				PacketModel packet = new PacketModel();
				if(total_index >= filesize)break;
				else{
					DataHeaderAnalysis(packet) ;
					if(LLHeaderAnalysis(packet)){
						IPPacketAnalysis(packet);
						if(packet.getProtocol2().equals("TCP")){
							TCPPacketAnalysis(packet) ;
						}else if(packet.getProtocol2().equals("UDP")){
							UDPPacketAnalysis(packet) ;
						}else {
							packet.setData(readByteToString((int)packet.getCaplen() + 16 - index)) ;
						}
					}else{
						if(Unknownhandle(packet)) ;
						else break ;
					}
					packets.add(packet) ;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return packets ;
	}

	private void UDPPacketAnalysis(PacketModel packet) {
		long port_source = readByteToLong(2);
		packet.setPort_source((int)port_source);

		long port_destination = readByteToLong(2);
		packet.setPort_destination((int)port_destination);

		long udp_length = readByteToLong(2) ;
		packet.setUdp_length((int)udp_length);

		String checksum = readByteToString(2) ;
		packet.setChecksum("0x"+checksum) ;

		String data = readByteToString((int)packet.getCaplen() + 16 - index);
		packet.setData(data) ;
	}

	private void TCPPacketAnalysis(PacketModel packet) {
		long port_source = readByteToLong(2);
		packet.setPort_source((int)port_source);

		long port_destination = readByteToLong(2);
		packet.setPort_destination((int)port_destination);

		String sequence_number = readByteToString(4);
		packet.setSequence_number(sequence_number) ;

		String acknowlegment_number = readByteToString(4) ;
		packet.setAcknowlegment_number(acknowlegment_number) ;

		int temp = readByteToInt() ;
		int header_length = (temp&0xf0)/16 ;
		packet.setHeader_length(header_length*4) ;

		temp = readByteToInt();
		String flag = Integer.toHexString(temp) ;
		packet.setFlag("0x0"+flag) ;

		long windows_size = readByteToLong(2);
		packet.setWindows_size((int)windows_size) ;

		String checksum = readByteToString(2) ;
		packet.setChecksum("0x"+checksum) ;

		readByteToString(2) ;

		if(packet.getHeader_length() == 32){
			TCPOptionAnalysis1(packet) ;
		}

		if(packet.getHeader_length() == 40){
			TCPOptionAnalysis2(packet) ;
		}

		String data = readByteToString((int)packet.getCaplen() + 16 - index);
		packet.setData(data) ;

		if(IsHttp(data)){
			packet.setProtocol2("HTTP");
			packet.setHttpData(data2http(data));
		}

		if(IsSip(data)){
			packet.setProtocol2("SIP");
			packet.setHttpData(data2http(data));
		}
	}

	private boolean IsSip(String data) {
		StringBuffer temp = new StringBuffer(data);
		int count = 0 ; 
		while(count < temp.length() - 6 ){
			String s = temp.substring(count, count + 9) ;
			if(s.equals("53 49 50 ")||s.equals("73 69 70 "))return true ;
			count += 3  ;
		}
		return false ;
	}

	private boolean IsHttp(String data) {
		StringBuffer temp = new StringBuffer(data);
		int count = 0 ; 
		while(count < temp.length() - 9){
			String s = temp.substring(count, count + 12) ;
			if(s.equals("48 54 54 50 "))return true ;
			count += 3  ;
		}
		return false ;
	}

	private String data2http(String data) {
		StringBuffer temp = new StringBuffer(data) ;
		char[] ch = new char[temp.length()/3] ;					
		for(int i = 0 ; i < temp.length() - 3 ; i+=3){
			String s = temp.substring(i, i + 2) ;
			int code = Integer.parseInt(s, 16) ;
			ch[i/3] = (char)code ;
		}
		String result = new String(ch) ;

		return result;
	}


	private void TCPOptionAnalysis2(PacketModel packet) {
		int mss_kind = readByteToInt() ;
		packet.setMss_kind(mss_kind) ;

		int mss_length =readByteToInt() ;
		packet.setMss_length(mss_length) ;

		long mss_value = readByteToLong(2) ;
		packet.setMss_value((int)mss_value) ;

		int tspo_kind = readByteToInt() ;
		packet.setTspo_kind(tspo_kind) ;

		int tspo_length = readByteToInt() ;
		packet.setTspo_length(tspo_length) ;

		int timestamp_kind = readByteToInt() ;
		packet.setTimestamp_kind(timestamp_kind) ;

		int timestamp_length = readByteToInt() ;
		packet.setTimestamp_length(timestamp_length) ;

		long timestamp_value = readByteToLong(4) ;
		packet.setTimestamp_value(timestamp_value) ;

		long timestamp_echo_reply = readByteToLong(4) ;
		packet.setTimestamp_echo_reply(timestamp_echo_reply) ;

		String options_type3 = readByteToString(1) ;
		if(options_type3.equals("01 "))packet.setOptions_type3("NO-Operation(NOP)") ;
		else packet.setOptions_type3("unknown") ;

		int ws_kind = readByteToInt() ;
		packet.setWs_kind(ws_kind) ;

		int ws_length = readByteToInt() ;
		packet.setWs_length(ws_length) ;

		int ws_short_count = readByteToInt() ;
		packet.setWs_short_count(ws_short_count) ;
	}

	private void TCPOptionAnalysis1(PacketModel packet) {
		String options_type1 = readByteToString(1) ;
		if(options_type1.equals("01 "))packet.setOptions_type1("NO-Operation(NOP)") ;
		else packet.setOptions_type1("unknown") ;

		String options_type2 = readByteToString(1) ;
		if(options_type2.equals("01 "))packet.setOptions_type2("NO-Operation(NOP)") ;
		else packet.setOptions_type2("unknown") ;

		int timestamp_kind = readByteToInt() ;
		packet.setTimestamp_kind(timestamp_kind) ;

		int timestamp_length = readByteToInt() ;
		packet.setTimestamp_length(timestamp_length) ;

		long timestamp_value = readByteToLong(4) ;
		packet.setTimestamp_value(timestamp_value) ;

		long timestamp_echo_reply = readByteToLong(4) ;
		packet.setTimestamp_echo_reply(timestamp_echo_reply) ;
	}

	private boolean Unknownhandle(PacketModel packet) {
		if(packet.getCaplen()>1500)return false ;
		else{
			int c = (int)packet.getCaplen() + 16 - index ;
			if(c < 0) return false ;
			else{
				String data = readByteToString(c);
				packet.setData(data);
			}
		}
		return true ;
	}

	private void IPPacketAnalysis(PacketModel packet) {				//ip包分析
		int temp1,temp2 ,temp3,temp4;

		int version_headerlen = readByteToInt() ;
		temp1 = (version_headerlen&0xf0)/16;
		temp2 = version_headerlen&0x0f;
		packet.setVersion(temp1) ;
		packet.setIp_header_len(temp2*4);

		String services_field = readByteToString(1);
		packet.setServices_field("0x"+services_field) ;

		long total_len = readByteToLong(2) ;
		packet.setTotal_len((int)total_len);

		long identification = readByteToLong(2) ;
		packet.setIdentification((int)identification) ;

		int flags_flagment_offset = readByteToInt() ;
		temp3 = (flags_flagment_offset&0xe0)/32;
		temp4 = flags_flagment_offset&0x1f;
		int flagment_offset = readByteToInt() ;
		packet.setFlags(temp3) ;
		packet.setFlagment_offset(temp4*256+flagment_offset);

		int time_to_live = readByteToInt();
		packet.setTime_to_live(time_to_live);

		int protocol2 = readByteToInt() ;
		packet.setProtocol2(WhatIsProtocol(protocol2));

		String header_checksum = readByteToString(2);
		packet.setHeader_checksum("0x"+header_checksum) ;

		String ip_source = readByteToIPaddress() ;
		packet.setIp_source(ip_source);

		String ip_destination = readByteToIPaddress() ;
		packet.setIp_destination(ip_destination);

	}

	private int readByteToInt() {
		int temp = 0 ;
		try {
			temp = in.read();
			index ++ ;
			total_index++;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}

	private String readByteToIPaddress() {
		int temp , c=0;
		StringBuffer s = new StringBuffer("") ;
		while(true){
			try {
				temp = in.read() ;
				index++;
				total_index++;
				s.append(temp);
				c++;
				if(c<4)s.append(".") ;
				else break ;
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		return s.toString();
	}

	private boolean LLHeaderAnalysis(PacketModel packet) {				//链路层头分析
		if(link_type.equals("71 ")){
			String temp = readByteToString(2) ;
			if(temp.equals("00 00 "))packet.setPacket_type("unicast to us (0)") ;
			else if(temp.equals("00 04 "))packet.setPacket_type("send by us (4)");
			else packet.setPacket_type("Unknown");

			long LinkLayer_address_type = readByteToLong(2) ;
			packet.setLinkLayer_address_type((int)LinkLayer_address_type) ;

			long LinkLayer_address_len = readByteToLong(2) ;
			packet.setLinkLayer_address_len((int)LinkLayer_address_len) ;

			readByteToString(8) ;

			String protocol1 = readByteToString(2) ;
			if(protocol1.equals("08 00 "))packet.setProtocol1("IP") ;
			else if(protocol1.equals("08 06 "))packet.setProtocol1("ARP") ;
			else if(protocol1.equals("86 dd "))packet.setProtocol1("IPv6") ;
			else {
				packet.setProtocol1("Unknown");
				return false ;
			}
		}else if(link_type.equals("01 ")){
			String mac_destination = readByteToMac(6) ;
			packet.setMac_destination(mac_destination) ;

			String mac_source = readByteToMac(6) ;
			packet.setMac_source(mac_source) ;

			String protocol1 = readByteToString(2) ;
			if(protocol1.equals("08 00 "))packet.setProtocol1("IP") ;
			else if(protocol1.equals("08 06 "))packet.setProtocol1("ARP") ;
			else if(protocol1.equals("86 dd "))packet.setProtocol1("IPv6") ;
			else {
				packet.setProtocol1("Unknown");
				return false ;
			}
		}
		return true ;
	}

	private String readByteToMac(int n) {
		int temp , c=0;
		StringBuffer s = new StringBuffer("") ;
		while(true){
			try {
				temp = in.read() ;
				index++;
				total_index++;
				s.append(Integer.toHexString(temp));
				c++;
				if(c<n)s.append(":") ;
				else break ;
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		return s.toString();
	}

	private void DataHeaderAnalysis(PacketModel packet) {			//数据包头分析
		long timestampHigh = readByteToLong(4);
		String TimestampHigh = TimeStampToDate(timestampHigh);
		packet.setTimestampHigh(TimestampHigh) ;

		long timestampLow = readByteToLong(4);
		if(timestampLow <= 99999)
			packet.setTimestampLow("0"+Long.toString(timestampLow));
		else
			packet.setTimestampLow(Long.toString(timestampLow));

		long caplen = readByteToLong(4);
		packet.setCaplen(caplen);

		long len = readByteToLong(4);
		packet.setLen(len) ;

	}

	private static long readByteToLong(int n) {				//读 n个字节，返回一个整型
		long[] k = new long[n] ;
		long result = 0 ;
		try {
			int c = 0 ;
			while(c<n){
				k[c] = in.read();
				index++;
				total_index++;
				if(n == 4)result+=k[c]*(Math.pow(2,c*8));
				else if(n==2)result+=k[c]*(Math.pow(2,(n-c-1)*8));
				c++ ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result ;
	}

	public boolean IsPcap(String fileName){                	 //pcap文件开始以d4 c3 b2 a1或a1 b2 c3 d4为标志
		file = new File(fileName);
		String s ="";
		try {
			in = new FileInputStream(file) ;
			s = readByteToString(4) ;
			in.close() ;
			file = null ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s.equals("d4 c3 b2 a1 ")||s.equals("a1 b2 c3 d4 ");	
	}

	public String readByteToString(int n){			//读 n个字节，返回一个字符串
		byte[] tempbytes = new byte[n] ;
		StringBuffer s = new StringBuffer("");
		try {
			in.read(tempbytes);
			int[] v = new int[n]; 
			int c = 0 ;
			while(c<n){
				v[c] = tempbytes[c]&0xFF;
				if(v[c]==0)s.append("00 ");
				else if(v[c]<16)s.append("0"+Integer.toHexString(v[c])+" ");
				else s.append(Integer.toHexString(v[c])+" ") ;
				c++ ;
				index++;
				total_index++;
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		String str = s.toString() ;
		return str;	
	}

	public String TimeStampToDate(long timestamp){  
		String date = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").
				format(new java.util.Date(timestamp*1000));  
		return date;  
	}  

	private String WhatIsProtocol(int protocol2) {				//判断IP包在传输层上的协议
		switch(protocol2){
		case 0:  return "HOPOPT";  		//IPv6逐跳选项
		case 1:  return "ICMP";  		    //控制消息
		case 2:  return "IGMP";   			//组管理
		case 3:  return "GGP";   			//网关对网关
		case 4:  return "IP in IP";  		//IP中的IP（封装）
		case 5:  return "ST";   			//流
		case 6:  return "TCP";   			//TCP传输控制
		case 7:  return "CBT";   			//CBT
		case 8:  return "EGP";   			//外部网关协议
		case 9:  return "IGB";   			//任何专用内部网关（Cisco将其用于IGRP）
		case 10: return "BBN-RCC-MON"; 	//BBN RCC监视
		case 11: return "NVP-II";  		//网络语音协议
		case 12: return "PUP";   			//PUP
		case 13: return "ARGUS";   		//ARGUS
		case 14: return "EMCON";   		//EMCON
		case 15: return "XNET";   			//跨网调试器
		case 16: return "CHAOS";   		//Chaos
		case 17: return "UDP";   			//用户数据报
		case 18: return "MUX";   			//多路复用
		case 19: return "DCN-MEAS";  		//DCN测量子系统
		case 20: return "HMP";   			//主机监视
		case 21: return "PRM";   			//数据包无线测量
		case 22: return "Xns_IDP";  		//XEROX NS IDP
		case 23: return "TRUNK-1";  		//第1主干
		case 24: return "TRUNK-2";  		//第2主干
		case 25: return "LEAF-1";  		//第1叶
		case 26: return "LEAF-2";  		//第2叶
		case 27: return "RDP";   			//可靠数据协议
		case 28: return "IRTP";   			//Internet可靠事务
		case 29: return "ISO-TP4";  		//ISO传输协议第4类
		case 30: return "NETBLT";  		//批量数据传输协议
		case 31: return "MFE-NSP";  		//MFE网络服务协议
		case 32: return "MERIT-INP"; 	    //MERIT节点间协议
		case 33: return "SEP";   			//顺序交换协议
		case 34: return "3PC";   			//第三方连接协议
		case 35: return "IDPR";   			//IDPR域间策略路由协议
		case 36: return "XTP";   			//XTP
		case 37: return "DDP";   			//数据报传送协议
		case 38: return "IDPR-CMTP"; 	    //IDPR控制消息传输协议
		case 39: return "TP++";   			//TP++传输协议
		case 40: return "IL";  		    //IL传输协议
		case 41: return "IPv6";   			//IPv6
		case 42: return "SDRP";   			//源要求路由协议
		case 43: return "IPv6-Route";		//IPv6的路由标头
		case 44: return "IPv6-Frag";  		//IPv6的片段标头
		case 45: return "IDRP";   			//域间路由协议
		case 46: return "RSVP";   			//保留协议
		case 47: return "GRE";   			// 通用路由封装
		case 48: return "MHRP";   			// 移动主机路由协议
		case 49: return "BNA";  			 // BNA
		case 50: return "ESP";  			 // IPv6 的封装安全负载
		case 51: return "AH";   			// IPv6 的身份验证标头
		case 52: return "I-NLSP";  		// 集成网络层安全性 TUBA
		case 53: return "SWIPE";   		// 采用加密的 IP
		case 54: return "NARP";   			// NBMA 地址解析协议
		case 55: return "MOBILE IP";  		// 移动性
		case 56: return "TLSP";   			// 传输层安全协议使用 Kryptonet 密钥管理
		case 57: return "SKIP";   			// SKIP
		case 58: return "IPv6-ICMP";  		// 用于 IPv6 的 ICMP
		case 59: return "IPv6-NoNxt"; 		// 用于 IPv6 的无下一个标头
		case 60: return "IPv6-Opts";  		// IPv6 的目标选项
		case 61: return "Anyone Betwen Hosts"; // 任意主机内部协议
		case 62: return "CFTP";   			// CFTP
		case 63: return "Anyone of LocalHost"; // 任意本地网络
		case 64: return "SAT-EXPAK";  		// SATNET 与后台 EXPAK
		case 65: return "KRYPTOLAN"; 		 // Kryptolan
		case 66: return "RVD MIT";  		// 远程虚拟磁盘协议
		case 67: return "IPPC";   			// Internet Pluribus 数据包核心
		case 68: return "Any Distributed File System"; //任意分布式文件系统
		case 69: return "SAT-MON"; 		 // SATNET 监视
		case 70: return "VISA";  			// VISA 协议
		case 71: return "IPCV";   			// Internet 数据包核心工具
		case 72: return "CPNX";   			// 计算机协议网络管理
		case 73: return "CPHB";   			// 计算机协议检测信号
		case 74: return "WSN";   			// 无线电脑网络
		case 75: return "PVP";   			// 数据包视频协议
		case 76: return "BR-SAT-MON"; 		// 后台 SATNET 监视
		case 77: return "SUN-ND";  		// SUN ND PROTOCOL-Temporary
		case 78: return "WB-MON";  		// WIDEBAND 监视
		case 79: return "WB-EXPAK"; 		 // WIDEBAND EXPAK
		case 80: return "ISO-IP";  		// ISO Internet 协议
		case 81: return "VMTP";   			// VMTP
		case 82: return "SECURE-VMTP";		 // SECURE-VMTP
		case 83: return "VINES VINES";
		case 84: return "TTP";   			// TTP
		case 85: return "NSFNET-IGP";		// NSFNET-IGP
		case 86: return "DGP";   			// 异类网关协议
		case 87: return "TCF";  			 // TCF
		case 88: return "EIGRP";  			 // EIGRP
		case 89: return "OSPFIGP";  		// OSPFIGP
		case 90: return "Sprite-RPC";		 // Sprite RPC 协议
		case 91: return "LARP";  			 // 轨迹地址解析协议
		case 92: return "MTP";   			// 多播传输协议
		case 93: return "AX.25";  			 // AX.25 帧
		case 94: return "IPIP";  			 // IP 中的 IP 封装协议
		case 95: return "MICP";   			// 移动互联控制协议
		case 96: return "SCC-SP";  			// 信号通讯安全协议
		case 97: return "ETHERIP"; 		 // IP 中的以太网封装
		case 98: return "ENCAP";   		// 封装标头
		case 99: return "Any Encrypt Plan"; //任意专用加密方案
		case 100: return "GMTP";   		// GMTP
		case 101: return "IFMP";   		// Ipsilon 流量管理协议
		case 102: return "PNNI";   		// IP 上的 PNNI
		case 103: return "PIM";  			 // 独立于协议的多播
		case 104: return "ARIS";   		// ARIS
		case 105: return "SCPS";  			 // SCPS
		case 106: return "QNX";   			// QNX
		case 107: return "A/N";   			// 活动网络
		case 108: return "IPComp";  		// IP 负载压缩协议
		case 109: return "SNP";   			// Sitara 网络协议
		case 110: return "Compaq-Peer"; 	// Compaq 对等协议
		case 111: return "IPX-in-IP";  	// IP 中的 IPX
		case 112: return "VRRP";   		// 虚拟路由器冗余协议
		case 113: return "PGM";   			// PGM 可靠传输协议
		case 114: return "Zero Hop Protocal"; //任意 0 跳协议
		case 115: return "L2TP";   		// 第二层隧道协议
		case 116: return "DDX";   			// D-II 数据交换 (DDX)
		case 117: return "IATP";   		// 交互式代理传输协议
		case 118: return "STP";   			// 计划传输协议
		case 119: return "SRP";   			// SpectraLink 无线协议
		case 120: return "UTI";   			// UTI
		case 121: return "SMP";   			// 简单邮件协议
		case 122: return "SM";   			// SM
		case 123: return "PTP";   			// 性能透明协议
		case 124: return "ISIS";   		// over IPv4
		case 125: return "FIRE";   		
		case 126: return "CRTP";   		// Combat 无线传输协议
		case 127: return "CRUDP";  		 // Combat 无线用户数据报
		case 128: return "SSCOPMCE";  
		case 129: return "IPLT";  
		case 130: return "SPS";   			// 安全数据包防护
		case 131: return "PIPE";   		// IP 中的专用 IP 封装
		case 132: return "SCTP";   		// 流控制传输协议
		case 133: return "FC";   			// 光纤通道
		case 255: return "保留";   
		default: return "未分配";
		} 
	}
}
