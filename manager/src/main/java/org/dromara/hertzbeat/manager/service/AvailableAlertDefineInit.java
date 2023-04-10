package org.dromara.hertzbeat.manager.service;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hertzbeat.alert.dao.AlertDefineDao;
import org.dromara.hertzbeat.common.entity.alerter.AlertDefine;
import org.dromara.hertzbeat.common.util.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * available alert define database record init
 *
 *
 */
@Service
@Order(value = 2)
@Slf4j
public class AvailableAlertDefineInit implements CommandLineRunner {

	@Autowired
	private AlertDefineDao alertDefineDao;

	@Autowired
	private AppService appService;

	@Override
	public void run(String... args) throws Exception {
		Set<String> apps = appService.getAllAppDefines().keySet();
		for (String app : apps) {
			try {
				Optional<AlertDefine> optional = alertDefineDao.queryAlertDefineByAppAndMetricAndField(app, CommonConstants.AVAILABILITY, null);
				if (optional.isEmpty()) {
					AlertDefine alertDefine = AlertDefine.builder()
							.app(app)
							.metric(CommonConstants.AVAILABILITY)
							.preset(true)
							.times(1)
							.enable(true)
							.priority(CommonConstants.ALERT_PRIORITY_CODE_EMERGENCY)
							.template("${app} monitoring availability alert, code is ${code}")
							.build();
					alertDefineDao.save(alertDefine);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
}
