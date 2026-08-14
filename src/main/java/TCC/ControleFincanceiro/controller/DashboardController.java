package TCC.ControleFincanceiro.controller;


import TCC.ControleFincanceiro.dto.dashboard.DashboardDTO;

import TCC.ControleFincanceiro.service.DashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {


    private final DashboardService dashboardService;


    /* ===================================================
       BUSCAR DASHBOARD
    =================================================== */

    @GetMapping
    public ResponseEntity<DashboardDTO> buscarDashboard(

            @RequestParam Long usuarioId,

            @RequestParam int mes,

            @RequestParam int ano

    ) {

        return ResponseEntity.ok(

                dashboardService.buscarDashboard(

                        usuarioId,

                        mes,

                        ano

                )

        );

    }

}