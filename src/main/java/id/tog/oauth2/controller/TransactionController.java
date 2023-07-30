package id.tog.oauth2.controller;

import id.tog.oauth2.entity.*;
import id.tog.oauth2.models.TransactionModel;
import id.tog.oauth2.repository.ChartRepository;
import id.tog.oauth2.repository.TransactionRepository;
import id.tog.oauth2.repository.UserRepository;
import id.tog.oauth2.responseException.BadRequest;
import id.tog.oauth2.util.PageableSpec;
import id.tog.oauth2.util.SpecificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    @Autowired
    ChartRepository chartRepository;


    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;


    @Secured({"ROLE_USER"})
    @PostMapping({"/buy","/buy/"})
    public Transaction buyTransaction(@RequestBody List<TransactionModel> transactionModels, HttpServletRequest request){
        User user = userRepository.findOneByUsername(request.getRemoteUser());
        if(transactionModels == null ) throw new BadRequest("Transaction required");
        if(transactionModels.size() == 0) throw new BadRequest("Transaction required");
        Transaction transaction = new Transaction();
        Integer totalAmount=0;
        Long totalCost= 0L;
        List<TransactionDetail> transactionDetails = new ArrayList<>();
        for (TransactionModel transactionModel : transactionModels){

            if(transactionModel.getChartId() == null) throw new BadRequest("chart id required");
            Chart chart = chartRepository.findByIdAndUser(transactionModel.getChartId(), user);
            if(chart == null)throw  new BadRequest("Chart dengan id "+transactionModel.getChartId()+" tidak ditemukan");

            TransactionDetail transactionDetail = new TransactionDetail();

            transactionDetail.setItemName(chart.getItem().getName());
            transactionDetail.setItemImage(chart.getItem().getImage());
            transactionDetail.setItemDescription(chart.getItem().getDescription());
            transactionDetail.setItemFee(chart.getItem().getPrice());
            transactionDetail.setAmount(chart.getAmount());
            transactionDetail.setTotalItemFee(chart.getItem().getPrice()*chart.getAmount());

            totalAmount=totalAmount + chart.getAmount();
            totalCost=totalCost + (chart.getItem().getPrice()*chart.getAmount());
            transactionDetails.add(transactionDetail);

        }
        transaction.setDetail(transactionDetails);
        transaction.setTotalAmount(totalAmount);
        transaction.setTotalCost(totalCost);
        transaction.setStatus("Pending");
        transaction.setTransactionCode(this.generateTransactionCode());
        transaction.setUser(user);

        transactionRepository.save(transaction);
        for (TransactionModel transactionModel : transactionModels){
            Chart chart = chartRepository.findById(transactionModel.getChartId()).orElse(null);
            chart.setDeleted(new Date());
            chartRepository.save(chart);
        }

        return transaction;
    }

    private String generateTransactionCode(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateTransaction = formatter.format(date);
        int countTransaction = 100000000;
        Integer countTransactionDB = transactionRepository.countAllByDeletedIsNull();
        if (countTransactionDB > 0 ) countTransaction += countTransactionDB;
        return "INV-"+date.getTime()+"-"+countTransaction;
    }



    @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN","ROLE_USER"})
    @GetMapping({"", "/"})
    public Page<Transaction> getTransaction(@RequestParam Map<String, String> params, HttpServletRequest request){
        PageableSpec pageableSpec= SpecificationUtils.of(params);
        Page<Transaction> transactions;
        if(request.isUserInRole("ROLE_USER")){
            Specification<Transaction> chekUser = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("username"), request.getRemoteUser());
            transactions = transactionRepository.findAll(pageableSpec.getSpecification().and(chekUser), pageableSpec.getPageable());
        }else{
            transactions = transactionRepository.findAll(pageableSpec.getSpecification(), pageableSpec.getPageable());
        }
        return transactions;
    }

    @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN","ROLE_USER"})
    @GetMapping({"/{transactionId:[\\d]+}", "/{transactionId:[\\d]+}/"})
    public Transaction getTransactionById(@PathVariable Long transactionId ){
        Transaction transaction=transactionRepository.findById(transactionId).orElseThrow(() -> new BadRequest("Transaction dengan id "+transactionId+" tidak ditemukan"));
        return transaction;
    }


}
