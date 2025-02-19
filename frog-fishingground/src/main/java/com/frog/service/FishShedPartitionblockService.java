package com.frog.service;

import com.frog.domain.PastureBatch;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;

import java.io.IOException;

public interface FishShedPartitionblockService {
    public boolean createBatch(PastureBatch pastureBatch) throws ABICodecException, TransactionBaseException, IOException;
}
