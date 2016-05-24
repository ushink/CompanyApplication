package com.publish.monitorsystem.api.readrfid;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.pow.api.cls.RfidPower;
import com.pow.api.cls.RfidPower.PDATYPE;
import com.publish.monitorsystem.application.SysApplication;
import com.uhf.api.cls.Reader;
import com.uhf.api.cls.Reader.AntPower;
import com.uhf.api.cls.Reader.AntPowerConf;
import com.uhf.api.cls.Reader.EmbededData_ST;
import com.uhf.api.cls.Reader.HoptableData_ST;
import com.uhf.api.cls.Reader.Inv_Potl;
import com.uhf.api.cls.Reader.Inv_Potls_ST;
import com.uhf.api.cls.Reader.Mtr_Param;
import com.uhf.api.cls.Reader.READER_ERR;
import com.uhf.api.cls.Reader.Region_Conf;
import com.uhf.api.cls.Reader.SL_TagProtocol;
import com.uhf.api.cls.Reader.TagFilter_ST;

public class ReadRFID implements IReadRFID{
	SysApplication myapp;
	public ReadRFID(SysApplication myapp) {
		this.myapp = (SysApplication) myapp;
	}
	
	@Override
	public void initReader() {
		myapp.Mreader = new Reader();
		PDATYPE PT = PDATYPE.valueOf(4);
		myapp.Rpower = new RfidPower(PT);
		boolean blen = myapp.Rpower.PowerUp();
		if (!blen)
			return;

		READER_ERR er = myapp.Mreader.InitReader_Notype(
				"/dev/ttyMT2", 1);
		Log.d("ckj", er.toString());
		if (er == READER_ERR.MT_OK_ERR) {
			myapp.antportc = 1;

			try {
				READER_ERR er1;
				myapp.Rparams = myapp.spf.ReadReaderParams();

				if (myapp.Rparams.invpro.size() < 1)
					myapp.Rparams.invpro.add("GEN2");

				List<SL_TagProtocol> ltp = new ArrayList<SL_TagProtocol>();
				for (int i = 0; i < myapp.Rparams.invpro.size(); i++) {
					if (myapp.Rparams.invpro.get(i).equals("GEN2")) {
						ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_GEN2);

					} else if (myapp.Rparams.invpro.get(i).equals("6B")) {
						ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_ISO180006B);

					} else if (myapp.Rparams.invpro.get(i).equals("IPX64")) {
						ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_IPX64);

					} else if (myapp.Rparams.invpro.get(i).equals("IPX256")) {
						ltp.add(SL_TagProtocol.SL_TAG_PROTOCOL_IPX256);

					}
				}

				Inv_Potls_ST ipst = myapp.Mreader.new Inv_Potls_ST();
				ipst.potlcnt = ltp.size();
				ipst.potls = new Inv_Potl[ipst.potlcnt];
				SL_TagProtocol[] stp = ltp
						.toArray(new SL_TagProtocol[ipst.potlcnt]);
				for (int i = 0; i < ipst.potlcnt; i++) {
					Inv_Potl ipl = myapp.Mreader.new Inv_Potl();
					ipl.weight = 30;
					ipl.potl = stp[i];
					ipst.potls[0] = ipl;
				}

				er1 = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_INVPOTL, ipst);
				Log.d("MYINFO", "Connected set pro:" + er1.toString());

				er1 = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT,
						new int[] { myapp.Rparams.checkant });
				Log.d("MYINFO", "Connected set checkant:" + er1.toString());

				AntPowerConf apcf = myapp.Mreader.new AntPowerConf();
				apcf.antcnt = myapp.antportc;
				for (int i = 0; i < apcf.antcnt; i++) {
					AntPower jaap = myapp.Mreader.new AntPower();
					jaap.antid = i + 1;
					jaap.readPower = (short) myapp.Rparams.rpow[i];
					jaap.writePower = (short) myapp.Rparams.wpow[i];
					apcf.Powers[i] = jaap;
				}

				myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);

				Region_Conf rre;
				switch (myapp.Rparams.region) {
				case 0:
					rre = Region_Conf.RG_PRC;
					break;
				case 1:
					rre = Region_Conf.RG_NA;
					break;
				case 2:
					rre = Region_Conf.RG_NONE;
					break;
				case 3:
					rre = Region_Conf.RG_KR;
					break;
				case 4:
					rre = Region_Conf.RG_EU;
					break;
				case 5:
				case 6:
				case 7:
				case 8:
				default:
					rre = Region_Conf.RG_NONE;
					break;
				}
				if (rre != Region_Conf.RG_NONE) {
					er1 = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rre);
				}

				if (myapp.Rparams.frelen > 0) {

					HoptableData_ST hdst = myapp.Mreader.new HoptableData_ST();
					hdst.lenhtb = myapp.Rparams.frelen;
					hdst.htb = myapp.Rparams.frecys;
					er1 = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst);
				}

				er1 = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION,
						new int[] { myapp.Rparams.session });
				er1 = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_Q,
						new int[] { myapp.Rparams.qv });
				er1 = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_WRITEMODE,
						new int[] { myapp.Rparams.wmode });
				er1 = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_POTL_GEN2_MAXEPCLEN,
						new int[] { myapp.Rparams.maxlen });
				er1 = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET,
						new int[] { myapp.Rparams.target });

				if (myapp.Rparams.filenable == 1) {
					TagFilter_ST tfst = myapp.Mreader.new TagFilter_ST();
					tfst.bank = myapp.Rparams.filbank;
					tfst.fdata = new byte[myapp.Rparams.fildata.length() / 2];
					myapp.Mreader.Str2Hex(myapp.Rparams.fildata,
							myapp.Rparams.fildata.length(), tfst.fdata);
					tfst.flen = tfst.fdata.length * 8;
					tfst.startaddr = myapp.Rparams.filadr;
					tfst.isInvert = myapp.Rparams.filisinver;

					myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER, tfst);
				}

				if (myapp.Rparams.emdenable == 1) {
					EmbededData_ST edst = myapp.Mreader.new EmbededData_ST();

					edst.accesspwd = null;
					edst.bank = myapp.Rparams.emdbank;
					edst.startaddr = myapp.Rparams.emdadr;
					edst.bytecnt = myapp.Rparams.emdbytec;
					edst.accesspwd = null;

					er1 = myapp.Mreader.ParamSet(
							Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst);
				}

				er1 = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYEMDDATA,
						new int[] { myapp.Rparams.adataq });
				er1 = myapp.Mreader.ParamSet(
						Mtr_Param.MTR_PARAM_TAGDATA_RECORDHIGHESTRSSI,
						new int[] { myapp.Rparams.rhssi });
				er1 = myapp.Mreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_SEARCH_MODE,
						new int[] { myapp.Rparams.invw });
			} catch (Exception ex) {
				Log.d("MYINFO",ex.getMessage() + ex.toString() + ex.getStackTrace());
			}
		}
		
	}
	
	@Override
	public void colseReader() {
		if(myapp.Mreader!=null)
			myapp.Mreader.CloseReader();
		myapp.Mreader = null;
		
	}
	
}
