/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.sermant.core.utils;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.exception.NetworkInterfacesCheckException;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NetworkUtils
 *
 * @author luanwenfei
 * @since 2022-03-19
 */
public class NetworkUtils {
    private static List<String> allNetworkIps = null;

    private static final Logger LOGGER = LoggerFactory.getLogger();

    private static final String LOCAL_HOST_IP = "127.0.0.1";

    private NetworkUtils() {
    }

    /**
     * Obtain the ip addresses of all network cards of a machine
     *
     * @return List
     */
    public static List<String> getAllNetworkIp() {
        if (allNetworkIps != null) {
            return allNetworkIps;
        }

        List<String> result = new ArrayList<String>();

        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            if (netInterfaces == null) {
                throw new NetworkInterfacesCheckException("netInterfaces is null");
            }
            InetAddress ip;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> nii = ni.getInetAddresses();
                while (nii.hasMoreElements()) {
                    ip = nii.nextElement();
                    if (!ip.getHostAddress().contains(":")) {
                        String str = ip.getHostAddress();
                        if (!str.startsWith(LOCAL_HOST_IP)) {
                            result.add(str);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            LOGGER.log(Level.SEVERE, "failed to get host ip address", e);
        }

        if (result.size() > 0) {
            allNetworkIps = result;
        }
        return allNetworkIps;
    }

    /**
     * Gets all the host names of the machine
     *
     * @return String
     */
    public static Optional<String> getHostName() {
        InetAddress ia;
        try {
            ia = InetAddress.getLocalHost();
            return Optional.ofNullable(ia.getHostName());
        } catch (UnknownHostException e) {
            return Optional.empty();
        }
    }

    /**
     * Obtain the IP address of Linux
     *
     * @return IP address
     */
    public static String getMachineIp() {
        try {
            for (Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
                    networkInterfaceEnumeration.hasMoreElements(); ) {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                String name = networkInterface.getName();
                if (name.contains("docker") || name.contains("lo")) {
                    continue;
                }
                String ip = resolveNetworkIp(networkInterface);
                if (!StringUtils.EMPTY.equals(ip)) {
                    return ip;
                }
            }
        } catch (SocketException exception) {
            LOGGER.warning("An exception occurred while getting the machine's IP address.");
        }
        LOGGER.severe("Can not acquire correct instance ip , it will be replaced by local ip!");
        return LOCAL_HOST_IP;
    }

    private static String resolveNetworkIp(NetworkInterface networkInterface) {
        for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses();
                enumIpAddr.hasMoreElements(); ) {
            InetAddress inetAddress = enumIpAddr.nextElement();
            if (!(inetAddress instanceof Inet4Address) || inetAddress.isLoopbackAddress()) {
                continue;
            }
            String ipaddress = inetAddress.getHostAddress();
            if (!StringUtils.EMPTY.equals(ipaddress) && !LOCAL_HOST_IP.equals(ipaddress)) {
                // Take the first IP address that meets the requirements
                return ipaddress;
            }
        }
        return StringUtils.EMPTY;
    }
}
