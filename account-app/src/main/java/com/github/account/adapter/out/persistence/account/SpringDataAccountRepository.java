package com.github.account.adapter.out.persistence.account;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataAccountRepository extends JpaRepository<AccountJpaEntity, String> {
}
