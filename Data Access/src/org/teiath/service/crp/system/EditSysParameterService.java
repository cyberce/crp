package org.teiath.service.crp.system;

import org.teiath.data.domain.sys.SysParameter;
import org.teiath.service.exceptions.ServiceException;

public interface EditSysParameterService {

	public void saveSysParameter(SysParameter sysParameter)
			throws ServiceException;

	public SysParameter getSysParameterById(Integer routeId)
			throws ServiceException;
}
