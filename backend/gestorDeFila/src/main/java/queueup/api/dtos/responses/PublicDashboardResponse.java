package queueup.api.dtos.responses;

import java.util.List;

public record PublicDashboardResponse(CurrentCalledTicket current,
        List<NextCalledTickets> next) {

}
