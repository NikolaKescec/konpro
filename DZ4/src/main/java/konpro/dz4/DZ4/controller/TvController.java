package konpro.dz4.DZ4.controller;

import konpro.dz4.DZ4.configuration.CustomUserPrincipal;
import konpro.dz4.DZ4.dto.Pair;
import konpro.dz4.DZ4.entity.TvProgramme;
import konpro.dz4.DZ4.entity.User;
import konpro.dz4.DZ4.repository.DeferredResultRepository;
import konpro.dz4.DZ4.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class TvController {

    private final UserService userService;

    private final DeferredResultRepository deferredResultRepository;

    @GetMapping("/")
    public ModelAndView index() {
        final CustomUserPrincipal customUserPrincipal =
            (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final User user = customUserPrincipal.getUser();

        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        modelAndView.addObject("tvProgrammes", userService.findAll());
        modelAndView.addObject("currentTime", LocalDateTime.now());
        modelAndView.addObject("registered",
            user.getTvProgrammes().stream().map(TvProgramme::getId).collect(Collectors.toSet()));

        return modelAndView;
    }

    @GetMapping(value = "/remaining", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public DeferredResult<List<Pair<String, Long>>> remaining() {
        final CustomUserPrincipal customUserPrincipal =
            (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return deferredResultRepository.saveDeferredResultForUserId(customUserPrincipal.getUser().getId());
    }

    @PostMapping("/register/{id}")
    @ResponseBody
    public void register(@PathVariable("id") long id) {
        final CustomUserPrincipal customUserPrincipal =
            (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        userService.register(customUserPrincipal.getUser().getId(), id);
    }

}
