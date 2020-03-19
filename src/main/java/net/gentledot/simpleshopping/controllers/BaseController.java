package net.gentledot.simpleshopping.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping(value = "/api/v1")
public class BaseController {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
}
