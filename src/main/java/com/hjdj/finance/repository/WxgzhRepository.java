package com.hjdj.finance.repository;

import com.hjdj.finance.beans.Wxgzh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Queue;

public interface WxgzhRepository extends JpaRepository<Wxgzh, Integer> {
}
