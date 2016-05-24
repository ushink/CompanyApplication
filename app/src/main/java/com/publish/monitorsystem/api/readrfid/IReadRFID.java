package com.publish.monitorsystem.api.readrfid;

public interface IReadRFID {

	/**
	 * 初始化读写器
	 */
	void initReader ();
	
	/**
	 * 关闭读写器
	 */
	void colseReader ();
	
}
