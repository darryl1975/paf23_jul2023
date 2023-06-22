package sg.edu.nus.iss.paf23_jul2023.model;

import java.sql.Date;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    private Integer id;

    private Integer customerId;

    @FutureOrPresent(message = "Borrow date must be current or a future date")
    private Date loanDate;

    @Future(message = "Return date must not happen on the borrow date")
    private Date returnDate;
}
