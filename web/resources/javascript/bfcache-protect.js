/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 window.addEventListener("pageshow", function (event) {
            if (event.persisted) {
                window.location.replace(window.location.href);
            }
        });


