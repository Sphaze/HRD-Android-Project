package com.example.prj1;

import java.io.IOException;

/**
 *  HRD IP Server C++ Protocol, Simon Brown

 Java implementation by Daniel Curtis
 https://code.google.com/p/java-hrd-api/
 
 [includes personal modifications]
 *
 */

public class Command extends BaseActivity{

	/** Java does not support unsigned integers - unsigned integers in Java are
	  declared using long variables **/

	private  final long UNSIGNED_INT_MASK=0xFFFFFFFFL;
	private  final long UNSIGNED_BYTE_MASK=0XFF;
	final long SANITY1=0x1234ABCD&UNSIGNED_INT_MASK;
	final long SANITY2=0xABCD1234&UNSIGNED_INT_MASK;


	final int HRD_MSG_BLOCK = 16;
	final int TCHAR = 2;

	private final long nSize; //unsigned 32 bit integer
	private final long nSanity1;
	private final long nSanity2;
	private final long nChecksum;
	private final StringBuilder strMessage = new StringBuilder();
	
	byte []temp;

	Command(final String messageTxt) {

		super(R.string.app_name);

		nSanity1=SANITY1;
		nSanity2=SANITY2;
		nChecksum=0L;
		strMessage.append(messageTxt);
		nSize=setSize();
	}

	protected void storeUnsignedInt(byte[] result, int idx, final long inSize) {
		result[idx++]=(byte) ((inSize)&UNSIGNED_BYTE_MASK);
		result[idx++]=(byte) ((inSize>>>8)&UNSIGNED_BYTE_MASK);  //don't need to count signed bit, use >>>
		result[idx++]=(byte) ((inSize>>>16)&UNSIGNED_BYTE_MASK);  //don't need to count signed bit, use >>>
		result[idx++]=(byte) ((inSize>>>24)&UNSIGNED_BYTE_MASK);  //don't need to count signed bit, use >>>
	}

	long getSize() {
		return nSize;
	}

	long setSize() {

		int nMsgBytes = HRD_MSG_BLOCK + TCHAR * strMessage.length() + 6;
		return (UNSIGNED_INT_MASK & nMsgBytes); //the 32bit unsigned integer mask enables all of the 32 bits (by ANDing it with the result)
	}

	void sendCommand(final RspHandler handler) {

		int idx=0;
		temp=new byte[(int) nSize];
		storeUnsignedInt(temp, idx, nSize);		//idx was indexed in the function as result[1] result[2] result[3] result[4] 
		idx+=4;
		storeUnsignedInt(temp, idx, nSanity1);		//idx was indexed in the function as result[5] result[6] result[7] result[8]  
		idx+=4;
		storeUnsignedInt(temp, idx, nSanity2);		//idx was indexed in the function as result[9] result[10] result[11] result[12]
		idx+=4;
		storeUnsignedInt(temp, idx, nChecksum);	//idx was indexed in the function as result[13] result[14] result[15] result[16]  
		idx+=4;

		final char[] text=strMessage.toString().toCharArray();
		
		for(int i=0; i<text.length; i++)
		{  
			/** pack the characters of the string commands into bytes **/

			final int intOfChar=(int) text[i];
			temp[idx++]=(byte) (intOfChar);
			temp[idx++]=(byte) (intOfChar>>>8);
		}
		for(int i=0; i < 4; i++)
		{
			temp[idx++]=0;
		}
			new Thread(new Runnable() {
				
				@Override
				public void run() 
				{
					try {
						client.send(temp, handler); //send the byte array to the server
					} catch (IOException e) 
					{	
						e.printStackTrace();
					}
				}
			}).start();
	}

	String getStringData() {
		return strMessage.toString();
	}

	@Override
	public String toString() {
		return strMessage.toString();
	}
}



