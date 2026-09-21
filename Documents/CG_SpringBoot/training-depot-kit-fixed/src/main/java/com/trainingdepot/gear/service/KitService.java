package com.trainingdepot.gear.service;

import jakarta.servlet.http.HttpSession;

import com.trainingdepot.gear.kit.Kit;
import com.trainingdepot.gear.kit.KitView;

public interface KitService {

    Kit getKit(HttpSession session);

    void add(HttpSession session, int productId, int quantity);

    void updateQuantity(HttpSession session, int productId, int quantity);

    void remove(HttpSession session, int productId);

    void clear(HttpSession session);

    /** Resolves the session kit against live product data - ready to render. */
    KitView view(HttpSession session);

    int totalUnitCount(HttpSession session);
}
