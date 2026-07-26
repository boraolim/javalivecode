package hogar.codelive.externalws.service;

import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import hogar.codelive.externalws.constants.LogConstants;
import hogar.codelive.externalws.external.SoapCalculatorClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculatorService {
    private final SoapCalculatorClient soapCalculatorClient;

    @CacheEvict(value = "calculatorOperationCache", allEntries = true)
    public CompletableFuture<Integer> getAddValue(int stockWarehouseA, int stockWarehouseB) {
        log.info(LogConstants.LOG_START_OPERATION, stockWarehouseA, stockWarehouseB);
        return soapCalculatorClient.ejecutarSuma(stockWarehouseA, stockWarehouseB)
            .thenApply(this::logAndReturnSumResult);
    }

    @CacheEvict(value = "calculatorOperationCache", allEntries = true)
    public CompletableFuture<Integer> getMultiplyValue(int stockWarehouseA, int stockWarehouseB) {
        log.info(LogConstants.LOG_START_OPERATION, stockWarehouseA, stockWarehouseB);  
        return soapCalculatorClient.executeMultiply(stockWarehouseA, stockWarehouseB)
            .thenApply(this::logAndReturnMultiplyResult);
    }

    @CacheEvict(value = "calculatorOperationCache", allEntries = true)
    public CompletableFuture<Integer> getDivideValue(int stockWarehouseA, int stockWarehouseB) {
        log.info(LogConstants.LOG_START_OPERATION, stockWarehouseA, stockWarehouseB);
        return soapCalculatorClient.executeDivide(stockWarehouseA, stockWarehouseB)
            .thenApply(this::logAndReturnDivideResult);
    }

    private int logAndReturnSumResult(int resultado) {
        log.info(LogConstants.LOG_MESSAGE_SUME, resultado);
        return resultado;
    }

    private int logAndReturnMultiplyResult(int resultado) {
        log.info(LogConstants.LOG_MESSAGE_MULTIPLY, resultado);
        return resultado;
    }

    private int logAndReturnDivideResult(int resultado) {
        log.info(LogConstants.LOG_MESSAGE_DIVIDE, resultado);
        return resultado;
    }
}
