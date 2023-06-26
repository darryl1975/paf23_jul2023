package sg.edu.nus.iss.paf23_jul2023.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.iss.paf23_jul2023.model.Customer;
import sg.edu.nus.iss.paf23_jul2023.model.Loan;
import sg.edu.nus.iss.paf23_jul2023.model.LoanDetails;
import sg.edu.nus.iss.paf23_jul2023.model.Video;
import sg.edu.nus.iss.paf23_jul2023.repo.CustomerRepo;
import sg.edu.nus.iss.paf23_jul2023.repo.LoanDetailsRepo;
import sg.edu.nus.iss.paf23_jul2023.repo.LoanRepo;
import sg.edu.nus.iss.paf23_jul2023.repo.VideoRepo;

// dirty read - reading uncommited change of a concurrent transaction
// non-repeatable read - get different values/rows on a concurrent transaction update
// phantom read - get different rows after executing a query if there is an update/insert transaction happening.

// Isolation properties
// READ_UNCOMMITTED: lowest level of isolation  (prone to direty read, nonrepeatable and phantom)
// READ_COMMITTED: prevents dirty read. need a requery if there a a transaction commited concurrently.
// REPEATABLE_READ: prevents dirty read and non-repeatable read. but will not prevent phantom record.
// SERIALIZABLE: prevents dirty read, nonrepeatable read and phantom records.

@Service
// @Transactional(propagation = Propagation.REQUIRED)
public class LoanService {
    @Autowired
    LoanRepo lRepo;

    @Autowired
    LoanDetailsRepo ldRepo;

    @Autowired
    CustomerRepo cRepo;

    @Autowired
    VideoRepo vRepo;

    @Transactional
    public Boolean loanVideo(Customer customer, List<Video> videos) {
        Boolean bLoanSuccessful = false;

        // pre-requisite
        // 1. retrieve all video records
        List<Video> allVideos = vRepo.findAll();

        // 1. check that all videos are available, count > 0
        Boolean bAvailable = true;
        for (Video video : videos) {
            List<Video> filteredVideoList = allVideos.stream().filter(v -> v.getId().equals(video.getId()))
                    .collect(Collectors.toList());

            if (!filteredVideoList.isEmpty()) {
                if (filteredVideoList.get(0).getAvailableCount() == 0) {
                    bAvailable = false;
                    // throw a custom exception/ built in exception
                } else {
                    // reducing the video quantity in the video table
                    // for the video that the user loan
                    Video updateVideoEnt = filteredVideoList.get(0);
                    updateVideoEnt.setAvailableCount(updateVideoEnt.getAvailableCount() - 1);
                    vRepo.updateVideo(updateVideoEnt);
                }
            }
        }

        // throw new IllegalAccessException("Simulating errors after updating Video
        // count...");

        // 2. create a loan record
        // 3. create the loan details that tie to the loan
        if (bAvailable) {
            Loan loan = new Loan();
            loan.setCustomerId(customer.getId());
            loan.setLoanDate(Date.valueOf(LocalDate.now()));

            Integer createdLoanId = lRepo.createLoan(loan);

            // throw new IllegalAccessException("Simulating errors after creating loan
            // record...");

            for (Video video : videos) {
                LoanDetails loandetails = new LoanDetails();
                loandetails.setLoanId(createdLoanId);
                loandetails.setVideoId(video.getId());

                ldRepo.createLoanDetails(loandetails);
            }

            // throw new IllegalAccessException("Simulating errors after creating loan details record...");

            bLoanSuccessful = true;
        }

        return bLoanSuccessful;
    }

}
