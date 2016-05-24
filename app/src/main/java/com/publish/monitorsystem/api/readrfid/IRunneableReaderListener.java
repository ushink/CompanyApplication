package com.publish.monitorsystem.api.readrfid;

import com.uhf.api.cls.Reader.READER_ERR;
import com.uhf.api.cls.Reader.TAGINFO;

public interface IRunneableReaderListener {
	/**
	 * 设置标签读写错误
	 */
	void setTagInventory_Raw (READER_ERR er);
	/**
	 * 设置单次读取个数
	 */
	void setReadoncecnt (int i);
	/**
	 * 设置响声
	 */
	void setReaderSound (String tag, TAGINFO tfs);
	/**
	 * 设置读写错误
	 */
	void setReaderError (READER_ERR er);
	/**
	 * 设置View内容
	 */
	void setView ();
	/**
	 * 设置共读取个数
	 */
	void setReadNum ();
}
