package com.expample.privacy;

import java.io.Serializable;

public class PacketModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*     数据包头16字节             */
	private String timestampHigh ;						// 时间戳高位，数据包头0-3字节
	private String timestampLow ;						// 时间戳低位，数据包头4-7字节
	private long caplen ;								// 当前数据区长度，数据包头8-11字节
	private long len ;									// 实际网络中的数据长度，12-15字节
	
	/*     链路层头16字节或14字节，wifi 14字节，3G 16字节       */
	private String packet_type ;						// 0-1字节，00 04是seng by us(4)
	private int LinkLayer_address_type ;				// 2-3字节
	private int LinkLayer_address_len ;					// 4-5字节
	private String protocol1 ;							// 14-15字节或 12-13字节，08 00:IP ,08 06:ARP
	
	private String mac_destination ;					// 0-5字节
	private String mac_source;							// 6-11字节

	/*     IP packet 包头     */
	private int version ;								// 0字节高4位，一般为4，指IPv4
	private int ip_header_len ;							// 0字节低4位,一般为5,即 4字节x5=20字节
	private String services_field ;						// 1字节，作用不详
	private int total_len ;								// 2-3字节，IP包总长度
	private int identification ;						// 4-5字节,标识
	private int flags ;									// 6字节高3位，标志
	private int flagment_offset ;						// 6字节低5位+7字节，片偏移
	private int time_to_live ;							// 8字节，生存时间
	private String protocol2 = "Unknown";				// 9字节，协议 
	private String header_checksum ;					// 10-11字节，首部检验和
	private String ip_source ;							// 12-15字节，源地址
	private String ip_destination ;						// 16-19字节，目的地址
	
	/*     TCP包头&UDP包头          */
	private int port_source ;							// 0-1字节，源端口号
	private int port_destination ;						// 2-3字节，目的端口号
	
	private int udp_length ;							// 4-5字节 ，UDP长度         UDP
	private String sequence_number ;					// 4-7字节，数据序号		TCP
	private String acknowlegment_number ;				// 8-11字节，确认序号	TCP
	private int header_length ;							// 12字节高4位 ，偏移 		TCP
	private String flag ;								// 13字节低6位 ，标志位		TCP
	private int windows_size ;							// 14-15字节， 窗口大小 	TCP
	private String checksum ;							// 16-17字节 ，包校验和    TCP
	/*      如果有选项 12字节          */
	private String options_type1 ;						// 20字节  
	private String options_type2 ;						// 21字节
	private int timestamp_kind ;						// 22字节
	private int timestamp_length ;						// 23字节
	private long timestamp_value ;						// 24-27字节
	private long timestamp_echo_reply ;					// 28-31字节
	/*      如果有选项 20字节          */
	private int mss_kind ;								// 20字节
	private int mss_length ;							// 21字节
	private int mss_value ;								// 22-23字节
	private int tspo_kind ;								// 24字节
	private int tspo_length;							// 25字节
	private String options_type3 ;						// 36字节
	private int ws_kind ;								// 37字节
	private int ws_length ;								// 38字节
	private int ws_short_count ;						// 39字节
	
	private String HttpData ;							// Http协议数据
	private String data ;								// 具体数据
	
	public String getHttpData() {
		return HttpData;
	}
	public void setHttpData(String httpData) {
		HttpData = httpData;
	}
	public int getMss_kind() {
		return mss_kind;
	}
	public void setMss_kind(int mss_kind) {
		this.mss_kind = mss_kind;
	}
	public int getMss_length() {
		return mss_length;
	}
	public void setMss_length(int mss_length) {
		this.mss_length = mss_length;
	}
	public int getMss_value() {
		return mss_value;
	}
	public void setMss_value(int mss_value) {
		this.mss_value = mss_value;
	}
	public int getTspo_kind() {
		return tspo_kind;
	}
	public void setTspo_kind(int tspo_kind) {
		this.tspo_kind = tspo_kind;
	}
	public int getTspo_length() {
		return tspo_length;
	}
	public void setTspo_length(int tspo_length) {
		this.tspo_length = tspo_length;
	}
	public String getOptions_type3() {
		return options_type3;
	}
	public void setOptions_type3(String options_type3) {
		this.options_type3 = options_type3;
	}
	public int getWs_kind() {
		return ws_kind;
	}
	public void setWs_kind(int ws_kind) {
		this.ws_kind = ws_kind;
	}
	public int getWs_length() {
		return ws_length;
	}
	public void setWs_length(int ws_length) {
		this.ws_length = ws_length;
	}
	public int getWs_short_count() {
		return ws_short_count;
	}
	public void setWs_short_count(int ws_short_count) {
		this.ws_short_count = ws_short_count;
	}
	public String getOptions_type1() {
		return options_type1;
	}
	public void setOptions_type1(String options_type1) {
		this.options_type1 = options_type1;
	}
	public String getOptions_type2() {
		return options_type2;
	}
	public void setOptions_type2(String options_type2) {
		this.options_type2 = options_type2;
	}
	public int getTimestamp_kind() {
		return timestamp_kind;
	}
	public void setTimestamp_kind(int timestamp_kind) {
		this.timestamp_kind = timestamp_kind;
	}
	public int getTimestamp_length() {
		return timestamp_length;
	}
	public void setTimestamp_length(int timestamp_length) {
		this.timestamp_length = timestamp_length;
	}
	public long getTimestamp_value() {
		return timestamp_value;
	}
	public void setTimestamp_value(long timestamp_value) {
		this.timestamp_value = timestamp_value;
	}
	public long getTimestamp_echo_reply() {
		return timestamp_echo_reply;
	}
	public void setTimestamp_echo_reply(long timestamp_echo_reply) {
		this.timestamp_echo_reply = timestamp_echo_reply;
	}
	public int getPort_source() {
		return port_source;
	}
	public void setPort_source(int port_source) {
		this.port_source = port_source;
	}
	public int getPort_destination() {
		return port_destination;
	}
	public void setPort_destination(int port_destination) {
		this.port_destination = port_destination;
	}
	public int getUdp_length() {
		return udp_length;
	}
	public void setUdp_length(int udp_length) {
		this.udp_length = udp_length;
	}
	public String getSequence_number() {
		return sequence_number;
	}
	public void setSequence_number(String sequence_number) {
		this.sequence_number = sequence_number;
	}
	public String getAcknowlegment_number() {
		return acknowlegment_number;
	}
	public void setAcknowlegment_number(String acknowlegment_number) {
		this.acknowlegment_number = acknowlegment_number;
	}
	public int getHeader_length() {
		return header_length;
	}
	public void setHeader_length(int header_length) {
		this.header_length = header_length;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public int getWindows_size() {
		return windows_size;
	}
	public void setWindows_size(int windows_size) {
		this.windows_size = windows_size;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	public String getTimestampHigh() {
		return timestampHigh;
	}
	public void setTimestampHigh(String timestampHigh) {
		this.timestampHigh = timestampHigh;
	}
	public String getTimestampLow() {
		return timestampLow;
	}
	public void setTimestampLow(String timestampLow) {
		this.timestampLow = timestampLow;
	}
	public long getCaplen() {
		return caplen;
	}
	public void setCaplen(long caplen) {
		this.caplen = caplen;
	}
	public long getLen() {
		return len;
	}
	public void setLen(long len) {
		this.len = len;
	}
	public String getPacket_type() {
		return packet_type;
	}
	public void setPacket_type(String packet_type) {
		this.packet_type = packet_type;
	}
	public int getLinkLayer_address_type() {
		return LinkLayer_address_type;
	}
	public void setLinkLayer_address_type(int linkLayer_address_type) {
		LinkLayer_address_type = linkLayer_address_type;
	}
	public int getLinkLayer_address_len() {
		return LinkLayer_address_len;
	}
	public void setLinkLayer_address_len(int linkLayer_address_len) {
		LinkLayer_address_len = linkLayer_address_len;
	}
	public String getProtocol1() {
		return protocol1;
	}
	public void setProtocol1(String protocol1) {
		this.protocol1 = protocol1;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getIp_header_len() {
		return ip_header_len;
	}
	public void setIp_header_len(int ip_header_len) {
		this.ip_header_len = ip_header_len;
	}
	public String getServices_field() {
		return services_field;
	}
	public void setServices_field(String services_field) {
		this.services_field = services_field;
	}
	public int getTotal_len() {
		return total_len;
	}
	public void setTotal_len(int total_len) {
		this.total_len = total_len;
	}
	public int getIdentification() {
		return identification;
	}
	public void setIdentification(int identification) {
		this.identification = identification;
	}
	public int getFlags() {
		return flags;
	}
	public void setFlags(int flags) {
		this.flags = flags;
	}
	public int getFlagment_offset() {
		return flagment_offset;
	}
	public void setFlagment_offset(int flagment_offset) {
		this.flagment_offset = flagment_offset;
	}
	public int getTime_to_live() {
		return time_to_live;
	}
	public void setTime_to_live(int time_to_live) {
		this.time_to_live = time_to_live;
	}
	public String getProtocol2() {
		return protocol2;
	}
	public void setProtocol2(String protocol2) {
		this.protocol2 = protocol2;
	}
	public String getHeader_checksum() {
		return header_checksum;
	}
	public void setHeader_checksum(String header_checksum) {
		this.header_checksum = header_checksum;
	}
	public String getIp_source() {
		return ip_source;
	}
	public void setIp_source(String ip_source) {
		this.ip_source = ip_source;
	}
	public String getIp_destination() {
		return ip_destination;
	}
	public void setIp_destination(String ip_destination) {
		this.ip_destination = ip_destination;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	public String getMac_destination() {
		return mac_destination;
	}
	public void setMac_destination(String mac_destination) {
		this.mac_destination = mac_destination;
	}
	public String getMac_source() {
		return mac_source;
	}
	public void setMac_source(String mac_source) {
		this.mac_source = mac_source;
	}
	@Override
	public String toString() {
		return "PacketModel [timestampHigh=" + timestampHigh
				+ ", timestampLow=" + timestampLow + ", caplen=" + caplen
				+ ", len=" + len + ", packet_type=" + packet_type
				+ ", LinkLayer_address_type=" + LinkLayer_address_type
				+ ", LinkLayer_address_len=" + LinkLayer_address_len
				+ ", protocol1=" + protocol1 + ", mac_destination="
				+ mac_destination + ", mac_source=" + mac_source + ", version="
				+ version + ", ip_header_len=" + ip_header_len
				+ ", services_field=" + services_field + ", total_len="
				+ total_len + ", identification=" + identification + ", flags="
				+ flags + ", flagment_offset=" + flagment_offset
				+ ", time_to_live=" + time_to_live + ", protocol2=" + protocol2
				+ ", header_checksum=" + header_checksum + ", ip_source="
				+ ip_source + ", ip_destination=" + ip_destination
				+ ", port_source=" + port_source + ", port_destination="
				+ port_destination + ", udp_length=" + udp_length
				+ ", sequence_number=" + sequence_number
				+ ", acknowlegment_number=" + acknowlegment_number
				+ ", header_length=" + header_length + ", flag=" + flag
				+ ", windows_size=" + windows_size + ", checksum=" + checksum
				+ ", options_type1=" + options_type1 + ", options_type2="
				+ options_type2 + ", timestamp_kind=" + timestamp_kind
				+ ", timestamp_length=" + timestamp_length
				+ ", timestamp_value=" + timestamp_value
				+ ", timestamp_echo_reply=" + timestamp_echo_reply
				+ ", mss_kind=" + mss_kind + ", mss_length=" + mss_length
				+ ", mss_value=" + mss_value + ", tspo_kind=" + tspo_kind
				+ ", tspo_length=" + tspo_length + ", options_type3="
				+ options_type3 + ", ws_kind=" + ws_kind + ", ws_length="
				+ ws_length + ", ws_short_count=" + ws_short_count
				+ ", HttpData=" + HttpData + ", data=" + data + "]";
	}
	
	public PacketModel(){
		super();
	}
	
	public String ShowDataHeader(){
		return "--------------------------------\n"+ " 捕获时间:\n  " + timestampHigh
				+ "." + timestampLow + "\n 捕获长度: " + caplen
				+ "\n 理论长度: " + len ;
	}
	
	public String ShowLLHeader3G(){
		return "\n\n--------------------------------\n"
				+ " 数据包类型: " + packet_type
				+ "\n 链路层地址类型: " + LinkLayer_address_type
				+ "\n 链路层地址长度: " + LinkLayer_address_len
				+ "\n 网络层协议类型: " + protocol1 ;
	}
	
	public String ShowLLHeaderWIFI(){
		return "\n\n--------------------------------\n"
				+ " 网络层协议类型: " + protocol1 + "\n 目的物理地址:\n     "
				+ mac_destination + "\n 源物理地址:\n     " + mac_source ;
	}
	
	public String ShowIPHeader(){
		return "\n\n--------------------------------\n"
				+ " 版本: "+ version + "\n 数据包头长度: " + ip_header_len
				+ "\n 服务类型: " + services_field + "\n 总长度: "
				+ total_len + "\n 标识: " + identification + "\n 标志: "
				+ flags + "\n 片偏移: " + flagment_offset
				+ "\n 生存时间: " + time_to_live + "\n 传输层协议类型: " + protocol2
				+ "\n 首部校验和: " + header_checksum + "\n 源ip地址: "
				+ ip_source + "\n 目的ip地址: " + ip_destination ;
	}
	
	public String ShowTCPHeader(){
		return "\n\n--------------------------------\n"
				+ " 源端口: " + port_source + "\n 目的端口: "
				+ port_destination + "\n 数据序号: " + sequence_number
				+ "\n 确认序号: " + acknowlegment_number
				+ "\n 偏移: " + header_length + "\n 标识位: " + flag
				+ "\n 窗口大小: " + windows_size + "\n 包校验和: " + checksum ;
	}
	
	public String ShowUDPHeader(){
		return "\n\n--------------------------------\n"
				+ " 源端口: " + port_source + "\n 目的端口: "
				+ port_destination + "\n UDP长度: " + udp_length
				+ "\n 包校验和: " + checksum ;
	}
	
	public String ShowData(){
		return "\n\n--------------------------------\n"
				+ " 数据:\n"+ data ;
	}
	
	public String ShowHttpData(){
		return "\n\n--------------------------------\n"
				+ " 数据:\n"+ HttpData ;
	}
	
	public String ShowOption12(){
		return "\n 选项: \n " + options_type1 + "\n "+ options_type2 
				+ "\n 类型: " + timestamp_kind
				+ "\n 长度: " + timestamp_length
				+ "\n 值: " + timestamp_value
				+ "\n 应答: " + timestamp_echo_reply  ;
	}
	
	public String ShowOption20(){
		return "\n 选项: \n " + "最大部分类: " + mss_kind + "\n 最大部分长度: " + mss_length
				+ "\n 最大部分值: " + mss_value + "\n 封装许可类: " + tspo_kind
				+ "\n 封装许可长度: " + tspo_length + "\n 时间戳类: " + timestamp_kind
				+ "\n 时间戳长度: " + timestamp_length + "\n 时间戳值: " + timestamp_value
				+ "\n 时间戳应答: " + timestamp_echo_reply +"\n " + options_type3 
				+ "\n 窗口规模类: " + ws_kind + "\n 窗口规模长度: "
				+ ws_length + "\n 窗口短时计时: " + ws_short_count ;
	}
	
	public String ShowInfo(){
		if(packet_type == null){
			if(!protocol1.equals("IP")){
				return ShowDataHeader() + ShowLLHeaderWIFI() + ShowData() ;
			}else if(protocol2.equals("TCP")){
				if(header_length == 40)return ShowDataHeader() + ShowLLHeaderWIFI() + ShowIPHeader() + ShowTCPHeader() + ShowOption20() + ShowData() ;
				else if(header_length == 32)return ShowDataHeader() + ShowLLHeaderWIFI() + ShowIPHeader() + ShowTCPHeader() + ShowOption12() + ShowData() ;
				else return ShowDataHeader() + ShowLLHeaderWIFI() + ShowIPHeader() + ShowTCPHeader() + ShowData() ;
			}else if(protocol2.equals("UDP")){
				return ShowDataHeader() + ShowLLHeaderWIFI() + ShowIPHeader() + ShowUDPHeader() + ShowData() ;
			}else if(protocol2.equals("HTTP")||protocol2.equals("SIP")){
				if(header_length == 40)return ShowDataHeader() + ShowLLHeaderWIFI() + ShowIPHeader() + ShowTCPHeader() + ShowOption20() + ShowHttpData() + ShowData();
				else if(header_length == 32)return ShowDataHeader() + ShowLLHeaderWIFI() + ShowIPHeader() + ShowTCPHeader() + ShowOption12() + ShowHttpData() + ShowData();
				else return ShowDataHeader() + ShowLLHeaderWIFI() + ShowIPHeader() + ShowTCPHeader() + ShowHttpData() + ShowData();
			}
			else return ShowDataHeader() + ShowLLHeaderWIFI() + ShowIPHeader() +  ShowData() ;
			
		}else{
			if(!protocol1.equals("IP")){
				return ShowDataHeader() + ShowLLHeader3G() + ShowData() ;
			}else if(protocol2.equals("TCP")){
				if(header_length == 40)return ShowDataHeader() + ShowLLHeader3G() + ShowIPHeader() + ShowTCPHeader() + ShowOption20() + ShowData() ;
				else if(header_length == 32)return ShowDataHeader() + ShowLLHeader3G() + ShowIPHeader() + ShowTCPHeader() + ShowOption12() + ShowData() ;
				else return ShowDataHeader() + ShowLLHeader3G() + ShowIPHeader() + ShowTCPHeader()  + ShowData() ;
			}else if(protocol2.equals("UDP")){
				return ShowDataHeader() + ShowLLHeader3G() + ShowIPHeader() + ShowUDPHeader() + ShowData() ;
			}else if(protocol2.equals("HTTP")||protocol2.equals("SIP")){
				if(header_length == 40)return ShowDataHeader() + ShowLLHeader3G() + ShowIPHeader() + ShowTCPHeader() + ShowOption20() + ShowHttpData() + ShowData() ;
				else if(header_length == 32)return ShowDataHeader() + ShowLLHeader3G() + ShowIPHeader() + ShowTCPHeader() + ShowOption12() + ShowHttpData() + ShowData();
				else return ShowDataHeader() + ShowLLHeader3G() + ShowIPHeader() + ShowTCPHeader() + ShowHttpData() + ShowData();
			}
			else return ShowDataHeader() + ShowLLHeader3G() + ShowIPHeader() +  ShowData() ;
			
		}
	}
}
