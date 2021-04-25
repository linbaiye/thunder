package org.fastj.thunder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service("testingService")
public class TestingService {

    private final ServiceOne serviceOne;

    private final ServiceTwo serviceTwo;

}

package org.fastj.thunder;
public class TestingServiceUT {

}