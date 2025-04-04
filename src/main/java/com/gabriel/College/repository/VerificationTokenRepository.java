package com.gabriel.College.repository;

import com.gabriel.College.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
	VerificationToken findByVerificationToken(String verificationToken);
}
