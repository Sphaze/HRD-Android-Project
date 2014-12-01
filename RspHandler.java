
package com.example.prj1;

import android.util.Log;

/**
 * Part of NioClient
 * http://rox-xmlrpc.sourceforge.net/niotut/
 * Modified by Daniel Curtis for HRD protocol
 */
public class RspHandler {

    static final int BYTE_MASK=0xFF;
    static final long three_sec_delay = 3000L;
    static final String ERROR="error";
    private byte[] rsp=null;
    private int length;

    /**
     *
     * @param rsp
     * @return
     */
    public synchronized boolean handleResponse(final byte[] rsp) {
        this.rsp = rsp;
        this.notify();
        return true;
    }


    /**
     * 
     * @return a String containing the response to the prior command.
     * 
     */
    public synchronized String waitForResponse() {
        while(this.rsp==null){
            try{
                long starttime = System.currentTimeMillis();
                Log.i("TESTS", "start time: " + starttime);
                
                this.wait(three_sec_delay);
                long waittime = System.currentTimeMillis()-starttime;
                
                Log.i("TESTS", "wait time: " + waittime);
                if (waittime > 3000)
                {
                    return ERROR;
                }
            } catch(InterruptedException e){
                return ERROR;
            }
        }

        if(rsp.length==(BYTE_MASK&rsp[0])){
            length=BYTE_MASK&rsp[0];
        } else{
            if(rsp.length>3){
                length=0;
                length+=BYTE_MASK&rsp[0];
                length+=(BYTE_MASK&rsp[1])<<8;
                length+=(BYTE_MASK&rsp[2])<<16;
                length+=(BYTE_MASK&rsp[3])<<24;
                if(rsp.length!=length){
                    return ERROR;
                }
            } else{
                return ERROR;
            }
        }
        if(validatePacket())
        {
            return (extractData());
        }
     
        return ERROR;
    }

    /**
     * 
     * @return a boolean indicating whether the packet is well formed
     */
    
    private boolean validatePacket() {
        boolean result=true;
        result = result && rsp[4]==(byte) 0xcd;
        result=result&&rsp[5]==(byte) 0xab;
        result=result&&rsp[6]==(byte) 0x34;
        result=result&&rsp[7]==(byte) 0x12;
        result=result&&rsp[8]==(byte) 0x34;
        result=result&&rsp[9]==(byte) 0x12;
        result=result&&rsp[10]==(byte) 0xcd;
        result=result&&rsp[11]==(byte) 0xab;
        for(int i=length-1; i>length-7; i--){
            result=result&&rsp[i]==0;
        }
        
        
        return result;
    }

    /**
     *
     * @return a String containing the szText as a Java String (trimmed)
     */
    private String extractData() {
        final byte[] result=new byte[length-22];
        System.arraycopy(rsp, 16, result, 0, length-22);
        final StringBuilder sb=new StringBuilder();
        int idx=0;
        
        for(int i=0; i<(result.length>>1); i++)
        {
            final char mychar=(char) result[idx++];
            idx++;
            sb.append(mychar);
        }
        return sb.toString().trim();
    }
}
