package com.example.demo.controllers;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.util.Optional;
import java.util.function.Supplier;
import com.example.demo.Tools.ExcelFileExporter;
import com.example.demo.Tools.Tools;
import com.example.demo.models.LoginUser;
import com.example.demo.models.Trailer;
import com.example.demo.repo.TrailerRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.securingweb.WebSecurityConfig;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


@Controller
public class MainController {


    @Autowired
    private TrailerRepository trailerRepository;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/admin", method = RequestMethod.GET)

    public String admin(Model model) {

            Iterable<LoginUser> users = userRepository.findAll();
            model.addAttribute("users", users);
            return "admin.html";

    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
          return "login.html";
    }


    @RequestMapping(value = "/browser_error", method = RequestMethod.GET)
    public String browser (Model model) {
        return "browser_error.html";
    }



    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {

       Iterable<Trailer> trailers = trailerRepository.findByOutTrcIsNull(Sort.by("inDate").ascending());
        model.addAttribute("trailers", trailers);
        return "arrived.html";
    }

    @GetMapping("/arrived")
    public String arrived (Model model) {

        Iterable<Trailer> trailers = trailerRepository.findByOutTrcIsNull(Sort.by("inDate").ascending());
        model.addAttribute("trailers", trailers);
        return "arrived.html";
    }






    @GetMapping("/error")
    public String error (Model model) {

          return "error.html";
    }

    @GetMapping("/depart")
    public String depart(Model model) {
        model.addAttribute("title", "Departure");
        //Iterable<Trailer> trailers = trailerRepository.findAll(Sort.by("inDate").ascending());
        Iterable<Trailer> trailers = trailerRepository.findTop100ByOutTrcNotNull(Sort.by("outDate").descending());
        model.addAttribute("trailers", trailers);
        return "depart.html";
    }

    @GetMapping("/all")
    public String all(Model model) {
        model.addAttribute("title", "Glavnaja stranica");
     
        return "all.html";
    }

// add Arrival permit
    @RequestMapping(value = "/trl/add", method = RequestMethod.POST)
    public String trlAdd (@RequestParam String inTrl, @RequestParam  Date inDate, @RequestParam String dep, @RequestParam String inTruck, @RequestParam String inName, @RequestParam String inLname, @RequestParam String status, @RequestParam String email, Model model) throws ParseException {






        System.out.println("DATE IS: "+inDate.toString());

        Iterable<Trailer> trailers = trailerRepository.findByOutTrcIsNullAndTrl(inTrl);
        List<Trailer> trl = new ArrayList<>();
        trailers.forEach(trl::add);

        if(trl.size()>0){
            model.addAttribute("error", "Error!!! Permission for trailer "+inTrl.toUpperCase()+" already exist, Please go back and check!!!");
            return "action_result.html";
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        Trailer trailer = new Trailer(inTrl, inTruck, inName, inLname, dep, inDate, currentUser, status );
        System.out.println("Model created");





        Tools tools = new Tools();

        if (email.equals("yes")){

            try {
                tools.createExcel("IMPORT",inName,inLname,inTruck,inTrl, inDate.toString());
                System.out.println("Excel created");
            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }

            tools.sendEmail("IMPORT", inTruck,inTrl,inName,inLname, inDate.toString(), status);

            trailerRepository.save(trailer);

            tools.delfile("IMPORT", inTruck,inTrl,inName,inLname, inDate.toString());


        } else if (email.equals("yes,no")){

            trailerRepository.save(trailer);

        }

        model.addAttribute("success", "Permission Sucessfully issued!!!");
        return "action_result.html";


    }

// Add departure pertmit
    @PostMapping(value = "/dep")
    public String trlDepart (@RequestParam Long id, @RequestParam Date outDate, @RequestParam String dep, @RequestParam String outTruck, @RequestParam String outTrl, @RequestParam String outName, @RequestParam String outLname, @RequestParam String email, Model model) throws Exception {
        Trailer trailer = trailerRepository.findById(id).orElseThrow(() -> new Exception("TRAILER not found"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

  

        if(trailer.getOutTrc()!=null){
            System.out.println("Out truck is: "+trailer.getOutTrc());
            model.addAttribute("error", "Error!!! Permission for trailer "+outTrl.toUpperCase()+" already issued. Please go back and check! ");
            return "action_result.html";
        } else {

            trailer.setOutDate(outDate);
            trailer.setOutTrc(outTruck);
            trailer.setOutName(outName);
            trailer.setOutLname(outLname);
            trailer.setOutDepartment(dep);
            trailer.setOperatorOut(currentUser);

            Tools tools = new Tools();

            if(email.equals("yes")){

                System.out.println("Sending email");

                try {
                    tools.createExcel("EXPORT",outName,outLname,outTruck,outTrl, outDate.toString());
                } catch (IOException | InvalidFormatException e) {
                    e.printStackTrace();
                }

                tools.sendEmail("EXPORT", outTruck,outTrl,outName,outLname, outDate.toString(), "");

                trailerRepository.save(trailer);

                tools.delfile("EXPORT", outTruck,outTrl,outName,outLname,outDate.toString());

            } else if(email.equals("yes,no")){


                trailerRepository.save(trailer);

            }


            model.addAttribute("success", "Permission Sucessfully issued!!!");
            return "action_result.html";
        }


    }

// edit Arrival pertmit
    @RequestMapping(value = "/editarr", method = RequestMethod.POST)
    public String trlEditArr (@RequestParam Long id, @RequestParam Date inDate, @RequestParam String dep, @RequestParam String trl, @RequestParam String inTruck, @RequestParam String inName, @RequestParam String inLname, @RequestParam String status, @RequestParam String email, Model model) throws ParseException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

       // java.util.Date utilDate = new SimpleDateFormat("dd-MM-yyyy").parse(inDate);
       // java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());


        Trailer trailer = null;
        try {
            trailer = trailerRepository.findById(id).orElseThrow(() -> new Exception("TRAILER not found"));
        } catch (Exception e) {
            model.addAttribute("error", "Error!!! Trailer not found. Please go back and check.");
            return "action_result.html";
        }

        if (trailer.getOutTrc()!=null){
            model.addAttribute("error", "Trailer has departure permission. If you want change arrival permission, please go back and remove arrival permision first.");
            return "action_result.html";
        }


        trailer.setTrl(trl);
        trailer.setInDate(inDate);
        trailer.setInTrc(inTruck);
        trailer.setInName(inName);
        trailer.setInLname(inLname);
       // trailer.setDepartment(dep);
        trailer.setStatus(status);
        trailer.setInDepartment(dep);

        trailer.setOperatorIn(currentUser);

        Tools tools = new Tools();



        if (email.equals("yes")){

            try {
                tools.createExcel("IMPORT",inName,inLname,inTruck,trl, inDate.toString());
            } catch (IOException | InvalidFormatException e) {

                e.printStackTrace();

            }

            tools.sendEmail("IMPORT", inTruck,trl,inName,inLname, inDate.toString(), status);

            trailerRepository.save(trailer);

            tools.delfile("IMPORT", inTruck,trl,inName,inLname, inDate.toString());

            System.out.println("Sending email");

        } else if (email.equals("yes,no")){

            System.out.println("Email not sent");

            trailerRepository.save(trailer);

        }




        model.addAttribute("success","Success!!! Permission for trailer "+trl.toUpperCase()+" successfully updated!");
        return "action_result.html";

    }


    @RequestMapping(value = "/loadtype", params = "load", method = RequestMethod.POST)
   public String load (Model model, @RequestParam Date datefrom, Date dateto ) throws ParseException {

      



       if (datefrom == null || dateto == null ){
           return "all.html";
       }

        Iterable<Trailer> trailers = trailerRepository.findTop1000ByInDateBetweenOrderByInDateDesc(datefrom, dateto);

        model.addAttribute("trailers", trailers);


        return "all.html";
   }



    @RequestMapping(value = "/removearr", method = RequestMethod.POST)
    public String trlRemoveArr (@RequestParam Long id, String trl,  Model model) throws Exception {

        Trailer trailer = null;
        trailer = trailerRepository.findByTrlAndId(trl, id);

        if(trailer.getOutTrc()==null){

            trailerRepository.deleteById(id);

            model.addAttribute("success","Success!!! Arrival permission for trailer "+trl.toUpperCase()+" succesfully removed");
            return "action_result.html";

        } else   {

            model.addAttribute("error","Error!!! Permission for trailer "+trl.toUpperCase()+" wasn't removed. Trailer not found. Please go back and check.");

            return "action_result.html";
        }




    }

    @RequestMapping(value = "/editdep", method = RequestMethod.POST)
    public String trlEditDep (@RequestParam Long id, @RequestParam Date outDate, @RequestParam String dep, @RequestParam String trl, @RequestParam String outTruck, @RequestParam String outName, @RequestParam String outLname, @RequestParam String email, Model model) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        Trailer trailer = trailerRepository.findById(id).orElseThrow(() -> new Exception("TRAILER not found"));

        if(trailer.getOutTrc()==null){
            model.addAttribute("error","Error!!! Departure permission for trailer "+trl.toUpperCase()+" not found. Please go back and check. ");
        return "action_result.html";
        }

        trailer.setOutDate(outDate);
        trailer.setOutTrc(outTruck);
        trailer.setOutName(outName);
        trailer.setOutLname(outLname);
        trailer.setOutDepartment(dep);
        trailer.setOperatorOut(currentUser);


        Tools tools = new Tools();

        if (email.equals("yes")){

            try {
                tools.createExcel("EXPORT",outName,outLname,outTruck,trl,outDate.toString());
            } catch (IOException | InvalidFormatException e) {
                model.addAttribute("error","Error!!! Permission wasn't updated. Something went wrong (Excel error). Please go back and try again.");
                return "action_result.html";
            }

            trailerRepository.save(trailer);

            tools.sendEmail("EXPORT", outTruck,trl,outName,outLname,outDate.toString(), "");

            tools.delfile("EXPORT", outTruck,trl,outName,outLname,outDate.toString());

        } else if (email.equals("yes,no")){

            trailerRepository.save(trailer);
        }


        model.addAttribute("success","Success!!! Departure permission for trailer "+trl.toUpperCase()+" successfully updated!");
        return "action_result.html";
    }

    @RequestMapping(value = "/removedep", method = RequestMethod.POST)
    public String trlRemoveDep (@RequestParam Long id, @RequestParam String outTrc, Model model) throws Exception {

        Iterable<Trailer> trailers = trailerRepository.findByOutTrcAndId(outTrc, id);

        int counter = 0;
        for (Object i : trailers) {
            counter++;
        }

        if (counter == 0){
            model.addAttribute("error","Error!!! Trailer not found. Probably permission already issued by another operator. Please go back and check");
            return "action_result.html";
        }





        Trailer trailer = trailerRepository.findById(id).orElseThrow(() -> new Exception("TRAILER not found"));
        trailer.setOutDate(null);
        trailer.setOutTrc(null);
        trailer.setOutName(null);
        trailer.setOutLname(null);
        trailer.setOutDepartment(null);
        trailer.setOperatorOut(null);
        trailerRepository.save(trailer);

        model.addAttribute("success","Success!!! Permission successfully removed!");

        return "action_result.html";
    }

    // add User
    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public String userAdd (@RequestParam String username, @RequestParam  String password, @RequestParam String role, Model model) throws ParseException {

    Optional<LoginUser> userChk = userRepository.findByUsername(username);

    if(userChk.isPresent()){
        model.addAttribute("error","Error. User already exist. Please go back and check!");
        return "action_result.html";
    }


        LoginUser user = new LoginUser(username, password, role);
        System.out.println("Model created");
        userRepository.save(user);



        return "redirect:/admin";


    }


    @RequestMapping(value = "/removeuser", method = RequestMethod.POST)
    public String userRemove (@RequestParam Long id, String username,  Model model) throws Exception {



        LoginUser user = userRepository.findByIdAndUsername(id, username);



        if(user.getUsername()!=null && user.getId()!=null){

            userRepository.deleteById(id);
            model.addAttribute("success","Success! User "+username+" successfully removed.");
            return "action_result.html";

        } else   {
            model.addAttribute("error","User not found, please go back and check!");
            return "action_result.html";
        }




    }


    @RequestMapping(value = "/edituser", method = RequestMethod.POST)
    public String trlEditArr (@RequestParam Long id, @RequestParam String username, @RequestParam String password, @RequestParam String role, Model model) throws ParseException {



        LoginUser user = null;
        try {
            user = userRepository.findByIdAndUsername(id, username);
        } catch (Exception e) {
            model.addAttribute("error","User not found, please go back and check!");
            return "action_result.html";
        }

        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        userRepository.save(user);

        model.addAttribute("success","Success! User "+username+" updated removed.");
        return "action_result.html";
    }


}
