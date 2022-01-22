package dev.haedhutner.binance.dto;

import java.util.ArrayList;

public class AccountInfoDTO {
    public int makerCommission;
    public int takerCommission;
    public int buyerCommission;
    public int sellerCommission;
    public boolean canTrade;
    public boolean canWithdraw;
    public boolean canDeposit;
    public long updateTime;
    public String accountType;
    public ArrayList<BalanceDTO> balances;
    public ArrayList<String> permissions;
}
