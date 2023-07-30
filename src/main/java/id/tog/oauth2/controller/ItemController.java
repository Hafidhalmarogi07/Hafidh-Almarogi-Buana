package id.tog.oauth2.controller;

import id.tog.oauth2.entity.Item;
import id.tog.oauth2.repository.ItemRepository;
import id.tog.oauth2.responseException.BadRequest;
import id.tog.oauth2.util.ImageUtils;
import id.tog.oauth2.util.PageableSpec;
import id.tog.oauth2.util.PropertiesUtils;
import id.tog.oauth2.util.SpecificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/item")
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;


    @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN","ROLE_USER"})
    @GetMapping({"", "/"})
    public Page<Item> getAllItem(@RequestParam Map<String, String> params){
        PageableSpec<Item> pageableSpec = SpecificationUtils.of(params);
        Page<Item> items = itemRepository.findAll(pageableSpec.getSpecification(), pageableSpec.getPageable());
        for (Item item : items){
            item.setImage(PropertiesUtils.CDN_BASEURL+item.getImage());
        }
        return items;
    }
    @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN", "ROLE_USER"})
    @GetMapping({"/{itemId:[\\d]+}", "/{itemId:[\\d]+}"})
    public Item getByIdItem(@PathVariable Long itemId){
        Item item=itemRepository.findById(itemId).orElseThrow(() -> new BadRequest("Item dengan id "+itemId+" tidak ditemukan"));
        item.setImage(PropertiesUtils.CDN_BASEURL+item.getImage());
        return  item;
    }
    @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN"})
    @PostMapping({"","/"})
    public Item postItem(@RequestBody Item newItem){
        if(newItem.getName().isEmpty()) throw new BadRequest("Nama item dibutuhkan");
        if(newItem.getImage().isEmpty()) throw new BadRequest("Gambar item dibutuhkan");
        if(newItem.getPrice() == null) throw new BadRequest("price dibutukan");
        if(newItem.getStock()== null) throw new BadRequest("stok dibutukan");
        newItem.setImage(ImageUtils.fromBase64(newItem.getImage(), PropertiesUtils.CDN_PATH+"/item", "/item"));

        return itemRepository.save(newItem);
    }
    @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN"})
    @PutMapping({"/{itemId:[\\d]+}", "/{itemId:[\\d]+}"})
    public Item putItem(@PathVariable Long itemId, @RequestBody Item data){
        Item item=itemRepository.findById(itemId).orElseThrow(() -> new BadRequest("item dengan id "+itemId+" tidak ditemukan"));
        if(data.getName() != null) item.setName(data.getName());
        if(data.getImage() != null) item.setImage(ImageUtils.fromBase64(data.getImage(), PropertiesUtils.CDN_PATH+"/item", "/item"));
        if(data.getDescription() != null) item.setDescription(data.getDescription());
        if(data.getPrice() != null) item.setPrice(data.getPrice());
        if(data.getStock() != null) item.setStock(data.getStock());

        String[] linkImage = item.getImage().split("cdn");
        if(linkImage.length == 2){
            item.setImage(linkImage[1]);
        }
        return itemRepository.save(item);
    }
    @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN"})
    @DeleteMapping({"/{itemId:[\\d]+}", "/{itemId:[\\d]+}"})
    public String deleteItem(@PathVariable Long itemId){
        Item item=itemRepository.findById(itemId).orElseThrow(() -> new BadRequest("Item dengan id "+itemId+" tidak ditemukan"));
        item.setDeleted(new Date());
        itemRepository.save(item);
        return "{\"success\":true}";
    }
}
