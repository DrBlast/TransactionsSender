package com.wavesplatform;

import com.wavesplatform.transactions.InvokeScriptTransaction;
import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.transactions.SetAssetScriptTransaction;
import com.wavesplatform.transactions.TransferTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.account.PublicKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.transactions.invocation.Arg;
import com.wavesplatform.transactions.invocation.Function;
import com.wavesplatform.transactions.invocation.StringArg;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class TestTxSender {


    @Test
    public void test(){
        PrivateKey smart1 = PrivateKey.fromSeed("remain");
        PrivateKey smart2 = PrivateKey.fromSeed("acc 1");
        Address recipient = Address.from((byte) 'D', PublicKey.from(smart2));

        InetSocketAddress node = new InetSocketAddress("136.243.54.187", 4868);

        TransferTransaction tx = TransferTransaction.builder(recipient, Amount.of(5)).chainId((byte) 'M').getSignedWith(smart1);
        byte[] txBytes = tx.toBytes();
        TxSender.sendAndWait(txBytes, node, 'M');
    }
//"putIfNew(72PGCrGtGE3StxrEu7vtDsYuBAvUACHi2i9frgqkgpB6) failed with GenericError(Fee for IssueTransaction (10 in WAVES) does not exceed minimal value of 100000000 WAVES.)",


    @Test
    public void test3(){
        PrivateKey pk = PrivateKey.fromSeed("remain");
        PrivateKey smart = PrivateKey.fromSeed("acc0");
        Address recipient = Address.from((byte) 'D', PublicKey.from(smart));

        InetSocketAddress node = new InetSocketAddress("116.203.102.150", 6860);
        List<Arg> args = new ArrayList<>();
        args.add(new StringArg("test"));
        Function f = Function.as("complexityThresholdFailedTxAfter1000", args);

        InvokeScriptTransaction tx = InvokeScriptTransaction.builder(recipient, f)
                .chainId((byte) 'D').getSignedWith(pk);
        System.out.println(tx.id());

        byte[] txBytes = tx.toBytes();
        TxSender.sendAndWait(txBytes, node, 'D');
    }

    @Test
    public void test2() throws InterruptedException {
        PrivateKey smart1 = PrivateKey.fromSeed("devnet1");
        PrivateKey smart2 = PrivateKey.fromSeed("acc0");
        Address recipient = Address.from((byte) 'D', PublicKey.from(smart2));

        InetSocketAddress node = new InetSocketAddress("159.69.5.108", 6860);
        IssueTransaction tx = IssueTransaction.builder("name", 10000000L, 1)
                .fee(140000000L)
                .chainId((byte)'D')
                .script(new Base64String("base64:CAEG32nosg=="))
                .getSignedWith(smart1);
        System.out.println(tx.assetId());
        byte[] txBytes = tx.toBytes();
        TxSender.sendAndWait(txBytes, node, 'D');
        Thread.sleep(1000);
        SetAssetScriptTransaction sast = SetAssetScriptTransaction.builder(tx.assetId(), new Base64String("base64:CAEG32nosg=="))
                .fee(10)
                .chainId((byte)'D')
                .getSignedWith(smart1);
        byte[] txBytes2 = sast.toBytes();
        TxSender.sendAndWait(txBytes2, node, 'D');




    }

}
