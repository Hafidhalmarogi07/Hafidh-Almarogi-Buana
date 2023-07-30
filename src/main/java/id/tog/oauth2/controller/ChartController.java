package id.tog.oauth2.controller;

import id.tog.oauth2.entity.Chart;
import id.tog.oauth2.entity.Item;
import id.tog.oauth2.entity.User;
import id.tog.oauth2.repository.ChartRepository;
import id.tog.oauth2.repository.ItemRepository;
import id.tog.oauth2.repository.UserRepository;
import id.tog.oauth2.responseException.BadRequest;
import id.tog.oauth2.util.PageableSpec;
import id.tog.oauth2.util.PropertiesUtils;
import id.tog.oauth2.util.SpecificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chart")
public class ChartController {

    @Autowired
    ChartRepository chartRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;



    @Secured({"ROLE_USER"})
    @GetMapping({"", "/"})
    public Page<Chart> getAllItem(@RequestParam Map<String, String> params, HttpServletRequest request){
        params.put("filters", "[[\"user.username\",\"=\",\""+request.getRemoteUser()+"\"]]");
        PageableSpec<Chart> pageableSpec = SpecificationUtils.of(params);
        return  chartRepository.findAll(pageableSpec.getSpecification(), pageableSpec.getPageable());

    }

    @Secured({"ROLE_USER"})
    @GetMapping({"/ListChart", "/ListChart/"})
    public List<Chart> getAllChartList( HttpServletRequest request){
        User user = userRepository.findOneByUsername(request.getRemoteUser());
        return  chartRepository.findAllByUser(user);
    }

    @Secured({"ROLE_USER"})
    @PostMapping({"/addChart","/addChart/"})
    public List<Chart> addListChart(@RequestBody List<Chart> chartList, HttpServletRequest request){
        User user = userRepository.findOneByUsername(request.getRemoteUser());
        if(chartList == null ) throw new BadRequest("chart required");
        if(chartList.size() == 0) throw new BadRequest("chart required");
        List<Chart> newListChart = new ArrayList<>();
        for (Chart chart : chartList){
            if(chart.getItem() == null) new BadRequest("chart item required");
            if(chart.getItem().getId() == null) new BadRequest("chart item id required");
            if(chart.getAmount() == null || chart.getAmount() ==0) new BadRequest("chart amount id required");
            Item item = itemRepository.findById(chart.getItem().getId()).orElseThrow(()->  new BadRequest("chart item id not found"));
            Chart chart1 = chartRepository.findByUserAndItem(user, item);
            if(chart1 != null){
                chart1.setAmount(chart1.getAmount()+chart.getAmount());
                chart1.setTotalItemFee(chart1.getAmount() * item.getPrice());
                newListChart.add(chartRepository.save(chart1));
            }else{
                chart.setTotalItemFee(chart.getAmount() * item.getPrice());
                newListChart.add(chartRepository.save(chart));
            }
        }
        return newListChart;
    }

    @Secured({"ROLE_USER"})
    @DeleteMapping({"/{chartId:[\\d]+}", "/{chartId:[\\d]+}"})
    public String deleteChart(@PathVariable Long chartId,  HttpServletRequest request){
        User user = userRepository.findOneByUsername(request.getRemoteUser());
        Chart chart = chartRepository.findByIdAndUser(chartId, user);
        if(chart == null)throw  new BadRequest("Chart dengan id "+chartId+" tidak ditemukan");
        chart.setDeleted(new Date());
        chartRepository.save(chart);
        return "{\"success\":true}";
    }

}
