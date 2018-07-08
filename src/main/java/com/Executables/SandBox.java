package com.Executables;

import com.esutil.OWL2NT;
import com.esutil.SentenseSpliter;
import com.htmlSentenceWindow.ESEngineSW;
import com.htmlSentenceWindow.ResultHitJsonSW;
import com.htmlSentenceWindowWithMeta.ESEngineSWM;
import com.htmlSentenceWindowWithMeta.ResultHitJsonSWM;

import java.util.Arrays;
import java.util.List;

public final class SandBox {

    private SandBox() {
    }

    public static void main(String[] args) {

        OWL2NT converter = new OWL2NT();
        List<List<String>> tripples = converter.parseEntities();
        ESEngineSWM esEngine = new ESEngineSWM();
        for (List<String> tri : tripples) {
            List<ResultHitJsonSWM> matchString = esEngine.searchEntitiesMixQuery(tri);
            if (matchString.size() > 0) {
                esEngine.saveResult(matchString);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("interrupted");
                }
            }
        }
//

//        String s = "The 2145 UPS output is possibly connected to an extra non-2145 load. 1166 The 2145 UPS-1U output load is unexpectedly high. 1170 2145 UPS electronics fault (reported by the 2145 UPS alarm bits). 1171 2145 UPS-1U electronics fault (reported by the 2145 UPS-1U alarm bits). 1175 A problem has occurred with the uninterruptible power supply frame fault (reported by uninterruptible power supply alarm bits). 1179 Too many drives attached to the system. 1180 2145 UPS battery fault (reported by 2145 UPS alarm bits). 1181 2145 UPS-1U battery fault (reported by 2145 UPS-1U alarm bits). 1182 Ambient temperature is too high during system startup. 1183 The nodes hardware configuration does not meet the minimum requirements. 1185 2145 UPS fault, with no specific FRU identified (reported by uninterruptible power supply alarm bits). 1186 A problem has occurred in the 2145 UPS-1U, with no specific FRU identified (reported by 2145 UPS-1U alarm bits). 1187 Node software is inconsistent or damaged 1188 Too many software crashes have occurred. 1189 The node is held in the service state. 1190 The 2145 UPS battery has reached its end of life. 1191 The 2145 UPS-1U battery has reached its end of life. 1192 Unexpected node error 1193 The UPS battery charge is not enough to allow the node to start. 1194 Automatic recovery of offline node has failed. 1195 Node missing. 1198 Detected hardware is not a valid configuration. 1200 The configuration is not valid. Too many devices, MDisks, or targets have been presented to the system. 1201 A flash drive requires a recovery. 1202 A flash drive is missing from the configuration. 1203 A duplicate Fibre Channel frame has been received. 1210 A local Fibre Channel port has been excluded. 1212 Power supply exceeded temperature threshold. 1213 Boot drive missing, out of sync, or failed. 1214 Boot drive is in the wrong slot. 1215 A flash drive is failing. 1216 SAS errors have exceeded thresholds. 1217 A flash drive has exceeded the temperature warning threshold. 1220 A remote Fibre Channel port has been excluded. 1230 A login has been excluded. 1260 SAS cable fault type 2. 1298 A node has encountered an error updating. 1310 A managed disk is reporting excessive errors. 1311 A flash drive is offline due to excessive errors. 1320 A disk I/O medium error has occurred. 1322 Data protection information mismatch. 1328 Encryption key required. 1330 A suitable managed disk (MDisk) or drive for use as a quorum disk was not found. 1335 Quorum disk not available. 1340 A managed disk has timed out. 1350 IB ports are not operational. 1360 A SAN transport error occurred. 1370 A managed disk error recovery procedure (ERP) has occurred. 1400 The 2145 cannot detect an Ethernet connection. 1403 External port not operational. 1450 Fewer Fibre Channel I/O ports operational. 1471 Interface card is unsupported. 1472 Boot drive is in an unsupported slot. 1473 The installed battery is at a hardware revision level that is not supported by the current code level. 1474 Battery is nearing end of life. 1475 Battery is too hot. 1476 Battery is too cold. 1550 A cluster path has failed. 1570 Quorum disk configured on controller that has quorum disabled 1600 Mirrored disk repair halted because of difference. 1610 There are too many copied media errors on a managed disk. 1620 A storage pool is offline. 1623 One or more MDisks on a controller are degraded. 1624 Controller configuration has unsupported RDAC mode. 1625 Incorrect disk controller configuration. 1627 The cluster has insufficient redundancy in its controller connectivity. 1630 The number of device logins was reduced. 1660 The initialization of the managed disk has failed. 1670 The CMOS battery on the system board failed. 1680 Drive fault type 1 1684 Drive is missing. 1686 Drive fault type 3. 1689 Array MDisk has lost redundancy. 1690 No spare protection exists for one or more array MDisks. 1691 A background scrub process has found an inconsistency between data and parity on the array. 1692 Array MDisk has taken a spare member that does not match array goals. 1693 Drive exchange required. 1695 Persistent unsupported disk controller configuration. 1700 Unrecovered remote copy relationship 1710 There are too many cluster partnerships. The number of cluster partnerships has been reduced. 1720 Metro Mirror (Remote copy) - Relationship has stopped and lost synchronization, for reason other than a persistent I/O error (LSYNC) 1740 Recovery encryption key not available. 1741 Flash module is predicted to fail. 1750 Array response time too high. 1780 Encryption key changes are not committed. 1800 The SAN has been zoned incorrectly. 1801 A node has received too many Fibre Channel logins from another node. 1802 Fibre Channel network settings 1804 IB network settings 1840 The managed disk has bad blocks. 1850 Compressed volume copy has bad blocks 1860 Thin-provisioned volume copy offline because of failed repair. 1862 Thin-provisioned volume copy offline because of corrupt metadata. 1865 Thin-provisioned volume copy offline because of insufficient space. 1870 Mirrored volume offline because a hardware read error has occurred. 1895 Unrecovered FlashCopy mappings 1900 A FlashCopy, Trigger Prepare command has failed because a cache flush has failed. 1910 A FlashCopy mapping task was stopped because of the error that is indicated in the sense data. 1920 Global and Metro Mirror persistent error. 1925 Cached data cannot be destaged. 1930 Migration suspended. 1940 HyperSwap® volume or consistency group has lost synchronization between sites. 1950 Unable to mirror medium error. 2008 A software downgrade has failed. 2010 A software update has failed. 2020 IP Remote Copy link unavailable. 2021 Partner cluster IP address unreachable. 2022 Cannot authenticate with partner cluster. 2023 Unexpected cluster ID for partner cluster. 2030 Software error. 2035 Drive has disabled protection information support. 2040 A software update is required. 2055 System reboot required. 2060 Manual discharge of batteries required. 2070 A drive has been detected in an enclosure that does not support that drive. 2100 A software error has occurred. 2115 Performance of external MDisk has changed 2258 System SSL certificate has expired. 2500 A secure shell (SSH) session limit for the cluster has been reached. 2550 Encryption key on USB flash drive removed 2555 Encryption key error on USB flash drive. 2600 The cluster was unable to send an email. 2601 Error detected while sending an email. 2700 Unable to access NTP network time server 2702 Check configuration settings of the NTP server on the CMM 3000 The 2145 UPS temperature is close to its upper limit. ";
//        List<String> ss = SentenseSpliter.split(s);
//        ss.size();

    }


}