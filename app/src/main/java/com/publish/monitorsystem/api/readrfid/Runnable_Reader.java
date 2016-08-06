package com.publish.monitorsystem.api.readrfid;

import android.os.SystemClock;
import android.util.Log;

import com.msystemlib.utils.LogUtils;
import com.pow.api.cls.RfidPower.PDATYPE;
import com.publish.monitorsystem.application.SysApplication;
import com.uhf.api.cls.Reader;
import com.uhf.api.cls.Reader.READER_ERR;
import com.uhf.api.cls.Reader.TAGINFO;

public class Runnable_Reader implements Runnable{
	
	private IRunneableReaderListener iRunneableReader;
	public void setOnReadListener(IRunneableReaderListener iRunneableReader){
		this.iRunneableReader = iRunneableReader;
	}
	
	
	
	SysApplication myapp;
	public Runnable_Reader(SysApplication myapp) {
		this.myapp = myapp;
	}
	
	public void run() {

		String[] tag = null;

		int[] tagcnt = new int[1];

		tagcnt[0] = 0;
		synchronized (this) {
			Log.d("MYINFO", "ManActivity..1");
			READER_ERR er = myapp.Mreader.TagInventory_Raw(
					myapp.Rparams.uants, myapp.Rparams.uants.length,
					(short) myapp.Rparams.readtime, tagcnt);
			Log.d("MYINFO",
					"read:" + er.toString() + " cnt:"
							+ String.valueOf(tagcnt[0]));
			if (er == READER_ERR.MT_OK_ERR) {
				if (tagcnt[0] > 0) {
					if(iRunneableReader != null){
						iRunneableReader.setReadoncecnt(tagcnt[0]);
					}
					tag = new String[tagcnt[0]];
					for (int i = 0; i < tagcnt[0]; i++) {
						TAGINFO tfs = myapp.Mreader.new TAGINFO();
						if (myapp.Rpower.GetType() == PDATYPE.SCAN_ALPS_ANDROID_CUIUS2) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						er = myapp.Mreader.GetNextTag(tfs);
						if (er == READER_ERR.MT_HARDWARE_ALERT_ERR_BY_TOO_MANY_RESET) {
							if(iRunneableReader != null){
								iRunneableReader.setReaderError(er);
							}
						}
						if (er == READER_ERR.MT_OK_ERR) {
							tag[i] = Reader.bytes_Hexstr(tfs.EpcId);
							if(iRunneableReader != null){
								iRunneableReader.setReaderSound(tag[i],tfs);
							}
						} else
							break;
					}
				}

			} else {
				if(iRunneableReader != null){
					iRunneableReader.setTagInventory_Raw(er);
				}
				return;
			}
		}

		if (tag == null) {
			tag = new String[0];
		}

		if (tagcnt[0] > 0){
			if(iRunneableReader != null){
				iRunneableReader.setView();
			}
		}
		if(iRunneableReader != null){
			iRunneableReader.setReadNum();
		}
	}
}
