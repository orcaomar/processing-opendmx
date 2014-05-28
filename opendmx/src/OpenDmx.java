package opendmx;
import com.sun.jna.ptr.*;
import com.sun.jna.Pointer;

/**
 * Class used to interact directly with OpenDMX hardware connected to a Windows machine.
 *
 * @author      Omar Khan
 */
public class OpenDmx {
  private Pointer port = null;
  private int device_number = -1;
  

  /**
   * Constructs an OpenDmx object on the given device number. If you only have 1 OpenDMX
   * device connected, then you should just input 0. 
   *
   * @param  device_number  number of the connected OpenDMX device. Input 0 if you have only 1 such device.
   */  
  public OpenDmx(int device_number) throws FTDIException {
    this.device_number = device_number;
    open();
  }
  
  private void open() throws FTDIException {
    PointerByReference port_ref = new PointerByReference();
    int res = FTDI_D2XX.INSTANCE.FT_Open((short)this.device_number, port_ref);
    checkRes(res);
    port = port_ref.getValue();
    
    res = FTDI_D2XX.INSTANCE.FT_ResetDevice(port);
    checkRes(res);
    
    res = FTDI_D2XX.INSTANCE.FT_SetDivisor(port, 12);
    checkRes(res);
   
    res = FTDI_D2XX.INSTANCE.FT_SetDataCharacteristics(port, FTDI_D2XX.FT_BITS_8, FTDI_D2XX.FT_STOP_BITS_2, FTDI_D2XX.FT_PARITY_NONE);
    checkRes(res);

    res = FTDI_D2XX.INSTANCE.FT_SetFlowControl(port, FTDI_D2XX.FT_FLOW_NONE, (byte)0, (byte)0);
    checkRes(res);
    res = FTDI_D2XX.INSTANCE.FT_ClrRts(port);
    checkRes(res);
    res = FTDI_D2XX.INSTANCE.FT_Purge(port, FTDI_D2XX.FT_PURGE_TX);
    checkRes(res);
    res = FTDI_D2XX.INSTANCE.FT_Purge(port, FTDI_D2XX.FT_PURGE_RX);
    checkRes(res);
  } 

  /**
   * Closes the currently open OpenDMX devices. 
   *
   * @return      An error code. Returns FTDI_D2XX.FT_OK if everything is fine, otherwise an error. See FTDI_D2XX class.
   */  
  public int close() {
    assert(port != null);
    return FTDI_D2XX.INSTANCE.FT_Close(port);    
  }
  
  /**
   * Sends data to the OpenDMX device. 
   *
   * @param  data  An array of data for channels 1->N, where N is no greater than 512.
   * @param  len_data The number of bytes to send, read from the beginning of the array. Must be between 0 and 512
   *
   * @return      An error code. Returns FTDI_D2XX.FT_OK if everything is fine, otherwise an error. See FTDI_D2XX class.
   */
  public int sendData(byte[] data, int len_data) {
    assert(port != null);
    assert(len_data >= 0 && len_data <= 512);
    FTDI_D2XX.INSTANCE.FT_SetBreakOn(port);
    FTDI_D2XX.INSTANCE.FT_SetBreakOff(port);
    IntByReference written = new IntByReference();
    byte[] code = {0};  
    int res = FTDI_D2XX.INSTANCE.FT_Write(port, code, 1, written);
    try {
      checkRes(res);
      res = FTDI_D2XX.INSTANCE.FT_Write(port, data, len_data, written);
      checkRes(res);
    } catch (FTDIException e) {

    }
    return res;
  }
  
  private void checkRes(int res) throws FTDIException {
    if (res != FTDI_D2XX.FT_OK) {
      throw new FTDIException("problem! check FTDI_D2XX.java for error code info. code: " + res);
    }
  }
}
