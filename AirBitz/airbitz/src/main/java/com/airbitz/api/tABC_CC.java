/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.airbitz.api;

public final class tABC_CC {
  public final static tABC_CC ABC_CC_Ok = new tABC_CC("ABC_CC_Ok", coreJNI.ABC_CC_Ok_get());
  public final static tABC_CC ABC_CC_Error = new tABC_CC("ABC_CC_Error", coreJNI.ABC_CC_Error_get());
  public final static tABC_CC ABC_CC_NULLPtr = new tABC_CC("ABC_CC_NULLPtr", coreJNI.ABC_CC_NULLPtr_get());
  public final static tABC_CC ABC_CC_NoAvailAccountSpace = new tABC_CC("ABC_CC_NoAvailAccountSpace", coreJNI.ABC_CC_NoAvailAccountSpace_get());
  public final static tABC_CC ABC_CC_DirReadError = new tABC_CC("ABC_CC_DirReadError", coreJNI.ABC_CC_DirReadError_get());
  public final static tABC_CC ABC_CC_FileOpenError = new tABC_CC("ABC_CC_FileOpenError", coreJNI.ABC_CC_FileOpenError_get());
  public final static tABC_CC ABC_CC_FileReadError = new tABC_CC("ABC_CC_FileReadError", coreJNI.ABC_CC_FileReadError_get());
  public final static tABC_CC ABC_CC_FileWriteError = new tABC_CC("ABC_CC_FileWriteError", coreJNI.ABC_CC_FileWriteError_get());
  public final static tABC_CC ABC_CC_FileDoesNotExist = new tABC_CC("ABC_CC_FileDoesNotExist", coreJNI.ABC_CC_FileDoesNotExist_get());
  public final static tABC_CC ABC_CC_UnknownCryptoType = new tABC_CC("ABC_CC_UnknownCryptoType", coreJNI.ABC_CC_UnknownCryptoType_get());
  public final static tABC_CC ABC_CC_InvalidCryptoType = new tABC_CC("ABC_CC_InvalidCryptoType", coreJNI.ABC_CC_InvalidCryptoType_get());
  public final static tABC_CC ABC_CC_DecryptError = new tABC_CC("ABC_CC_DecryptError", coreJNI.ABC_CC_DecryptError_get());
  public final static tABC_CC ABC_CC_DecryptFailure = new tABC_CC("ABC_CC_DecryptFailure", coreJNI.ABC_CC_DecryptFailure_get());
  public final static tABC_CC ABC_CC_EncryptError = new tABC_CC("ABC_CC_EncryptError", coreJNI.ABC_CC_EncryptError_get());
  public final static tABC_CC ABC_CC_ScryptError = new tABC_CC("ABC_CC_ScryptError", coreJNI.ABC_CC_ScryptError_get());
  public final static tABC_CC ABC_CC_AccountAlreadyExists = new tABC_CC("ABC_CC_AccountAlreadyExists", coreJNI.ABC_CC_AccountAlreadyExists_get());
  public final static tABC_CC ABC_CC_AccountDoesNotExist = new tABC_CC("ABC_CC_AccountDoesNotExist", coreJNI.ABC_CC_AccountDoesNotExist_get());
  public final static tABC_CC ABC_CC_JSONError = new tABC_CC("ABC_CC_JSONError", coreJNI.ABC_CC_JSONError_get());
  public final static tABC_CC ABC_CC_BadPassword = new tABC_CC("ABC_CC_BadPassword", coreJNI.ABC_CC_BadPassword_get());
  public final static tABC_CC ABC_CC_WalletAlreadyExists = new tABC_CC("ABC_CC_WalletAlreadyExists", coreJNI.ABC_CC_WalletAlreadyExists_get());
  public final static tABC_CC ABC_CC_URLError = new tABC_CC("ABC_CC_URLError", coreJNI.ABC_CC_URLError_get());
  public final static tABC_CC ABC_CC_SysError = new tABC_CC("ABC_CC_SysError", coreJNI.ABC_CC_SysError_get());
  public final static tABC_CC ABC_CC_NotInitialized = new tABC_CC("ABC_CC_NotInitialized", coreJNI.ABC_CC_NotInitialized_get());
  public final static tABC_CC ABC_CC_Reinitialization = new tABC_CC("ABC_CC_Reinitialization", coreJNI.ABC_CC_Reinitialization_get());
  public final static tABC_CC ABC_CC_ServerError = new tABC_CC("ABC_CC_ServerError", coreJNI.ABC_CC_ServerError_get());
  public final static tABC_CC ABC_CC_NoRecoveryQuestions = new tABC_CC("ABC_CC_NoRecoveryQuestions", coreJNI.ABC_CC_NoRecoveryQuestions_get());
  public final static tABC_CC ABC_CC_NotSupported = new tABC_CC("ABC_CC_NotSupported", coreJNI.ABC_CC_NotSupported_get());
  public final static tABC_CC ABC_CC_MutexError = new tABC_CC("ABC_CC_MutexError", coreJNI.ABC_CC_MutexError_get());
  public final static tABC_CC ABC_CC_NoTransaction = new tABC_CC("ABC_CC_NoTransaction", coreJNI.ABC_CC_NoTransaction_get());
  public final static tABC_CC ABC_CC_ParseError = new tABC_CC("ABC_CC_ParseError", coreJNI.ABC_CC_ParseError_get());
  public final static tABC_CC ABC_CC_InvalidWalletID = new tABC_CC("ABC_CC_InvalidWalletID", coreJNI.ABC_CC_InvalidWalletID_get());
  public final static tABC_CC ABC_CC_NoRequest = new tABC_CC("ABC_CC_NoRequest", coreJNI.ABC_CC_NoRequest_get());
  public final static tABC_CC ABC_CC_InsufficientFunds = new tABC_CC("ABC_CC_InsufficientFunds", coreJNI.ABC_CC_InsufficientFunds_get());
  public final static tABC_CC ABC_CC_Synchronizing = new tABC_CC("ABC_CC_Synchronizing", coreJNI.ABC_CC_Synchronizing_get());
  public final static tABC_CC ABC_CC_NonNumericPin = new tABC_CC("ABC_CC_NonNumericPin", coreJNI.ABC_CC_NonNumericPin_get());
  public final static tABC_CC ABC_CC_NoAvailableAddress = new tABC_CC("ABC_CC_NoAvailableAddress", coreJNI.ABC_CC_NoAvailableAddress_get());
  public final static tABC_CC ABC_CC_PinExpired = new tABC_CC("ABC_CC_PinExpired", coreJNI.ABC_CC_PinExpired_get());

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static tABC_CC swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + tABC_CC.class + " with value " + swigValue);
  }

  private tABC_CC(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private tABC_CC(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private tABC_CC(String swigName, tABC_CC swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static tABC_CC[] swigValues = { ABC_CC_Ok, ABC_CC_Error, ABC_CC_NULLPtr, ABC_CC_NoAvailAccountSpace, ABC_CC_DirReadError, ABC_CC_FileOpenError, ABC_CC_FileReadError, ABC_CC_FileWriteError, ABC_CC_FileDoesNotExist, ABC_CC_UnknownCryptoType, ABC_CC_InvalidCryptoType, ABC_CC_DecryptError, ABC_CC_DecryptFailure, ABC_CC_EncryptError, ABC_CC_ScryptError, ABC_CC_AccountAlreadyExists, ABC_CC_AccountDoesNotExist, ABC_CC_JSONError, ABC_CC_BadPassword, ABC_CC_WalletAlreadyExists, ABC_CC_URLError, ABC_CC_SysError, ABC_CC_NotInitialized, ABC_CC_Reinitialization, ABC_CC_ServerError, ABC_CC_NoRecoveryQuestions, ABC_CC_NotSupported, ABC_CC_MutexError, ABC_CC_NoTransaction, ABC_CC_ParseError, ABC_CC_InvalidWalletID, ABC_CC_NoRequest, ABC_CC_InsufficientFunds, ABC_CC_Synchronizing, ABC_CC_NonNumericPin, ABC_CC_NoAvailableAddress, ABC_CC_PinExpired };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}

