package com.bootcamp.ms_accounts.infrastructure.factory;

import com.bootcamp.ms_accounts.domain.model.enums.CustomerType;
import com.bootcamp.ms_accounts.domain.model.enums.ProfileType;
import com.bootcamp.ms_accounts.domain.strategy.AccountOpeningStrategy;
import com.bootcamp.ms_accounts.domain.strategy.PYMEBusinessStrategy;
import com.bootcamp.ms_accounts.domain.strategy.StandardBusinessStrategy;
import com.bootcamp.ms_accounts.domain.strategy.StandardPersonalStrategy;
import com.bootcamp.ms_accounts.domain.strategy.VIPPersonalStrategy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountStrategyFactory {

    public static AccountOpeningStrategy createPersonalStrategy(ProfileType profileType) {
        log.info("Creating Personal account strategy for profile: {}", profileType);

        return switch (profileType) {
            case STANDARD -> {
                log.debug("Creating StandardPersonalStrategy");
                yield new StandardPersonalStrategy();
            }
            case VIP -> {
                log.debug("Creating VIPPersonalStrategy");
                yield new VIPPersonalStrategy();
            }
            case PYME -> {
                log.error("PYME profile not supported for Personal customers");
                throw new IllegalArgumentException("PYME profile only available for Business customers");
            }
        };
    }

    public static AccountOpeningStrategy createBusinessStrategy(ProfileType profileType) {
        log.info("Creating Business account strategy for profile: {}", profileType);

        return switch (profileType) {
            case STANDARD -> {
                log.debug("Creating StandardBusinessStrategy");
                yield new StandardBusinessStrategy();
            }
            case PYME -> {
                log.debug("Creating PYMEBusinessStrategy");
                yield new PYMEBusinessStrategy();
            }
            case VIP -> {
                log.error("VIP profile not supported for Business customers");
                throw new IllegalArgumentException("VIP profile only available for Personal customers");
            }
        };
    }

    public static AccountOpeningStrategy createStrategy(CustomerType customerType, ProfileType profileType) {
        log.info("Creating account strategy for customerType={}, profileType={}", customerType, profileType);

        return switch (customerType) {
            case PERSONAL -> createPersonalStrategy(profileType);
            case BUSINESS -> createBusinessStrategy(profileType);
            default -> throw new IllegalArgumentException("Unsupported customer type: " + customerType);
        };
    }
}
