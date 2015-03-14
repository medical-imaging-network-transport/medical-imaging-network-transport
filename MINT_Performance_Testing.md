#### Contact information: ####
Adam J. Weigold<br>
Software Engineer<br>
Vital Images, Inc<br>
aweigold@vitalimages.com<br>
<hr />
<h1>MINT Performance Testing</h1>
<h2>Summary</h2>
When running MINT on Windows ensure that HTTP requests are handled by the HTTP.sys kernel driver.  Serving HTTP without HTTP.sys causes significant latency due to user/kernel mode switching<sup>1</sup>.<br>
<br>
<h2>Testing environment</h2>
Server:<br>
<ul><li>Dual Intel Xeon E5620 (2.40GHz, 2400Mhz, 4 Cores, 8 Logical Processors)<br>
</li><li>12 GB Physical RAM<br>
</li><li>Intel 82574L Gigabit Network Interface.<br>
Client system was running Red Hat Enterprise Linux 5.5 (RHEL) and connected via crossover Ethernet directly to the server.  Curl was used to measure transport.  Curl redirected output to /dev/null to ensure client disk latency was not a factor.</li></ul>

Ex:<br>
<code>curl -o /dev/null http://{ip}/MINT/studies/{uuid}/DICOM/binaryitems/all</code>

To configure the IIS AJP ISAPI redirector:<br>
<ul><li>Generic how-to: <a href='http://tomcat.apache.org/connectors-doc/webserver_howto/iis.html'>http://tomcat.apache.org/connectors-doc/webserver_howto/iis.html</a>
</li><li>IIS 7.0 how-to: <a href='http://www.iisadmin.co.uk/?p=72'>http://www.iisadmin.co.uk/?p=72</a>
<h2>Testing methodology</h2>
Server was loaded with Windows 2003 Standard <a href='https://code.google.com/p/medical-imaging-network-transport/source/detail?r=2'>R2</a> x64 SP2, Windows 2008 Enterprise <a href='https://code.google.com/p/medical-imaging-network-transport/source/detail?r=2'>R2</a> SP1, and Red Hat Enterprise Linux 5.5 (RHEL).<br />
For each test, 6 runs were performed, with the first one always being thrown out to ensure the data was cached in memory by the OS.<br />
The same study was used for all test runs, with a request of <code>http://{ip}/MINT/studies/{uuid}/DICOM/binaryitems/all</code>.  The study used represented 1090M of binary data.  For testing throughput on non-MINT transfers, the binary items were concatenated into a single file and served out.<br />
For Windows operating systems four tests were run:<br>
</li><li>MINT get all binary items request served directly by Tomcat.<br>
</li><li>Binaryitems file served by Apache httpd.<br>
</li><li>Binaryitems file served by ftp via IIS.<br>
</li><li>MINT get all binary items request served by IIS with an AJP ISAPI redirect to Tomcat.<br>
Windows 2003 tests ran IIS 6.0.  Windows 2008 tests ran IIS 7.0.  For IIS 6.0, there is a known performance issue with ISAPI redirects (<a href='http://support.microsoft.com/kb/906977/'>http://support.microsoft.com/kb/906977/</a>).  The testing was run with the change to <code>MaxBufferedSendBytes</code> as instructed by the kb article.<br />
For the RHEL operating system three tests were run:<br>
</li><li>MINT get all binary items request served directly by Tomcat.<br>
</li><li>Binaryitems file served by Apache httpd.<br>
</li><li>Binaryitems file served by vsftp.<br>
<h2>Test Results</h2>
The tests results show that when using MINT on a Windows operating system, you must ensure that HTTP is handled by HTTP.sys for maximum performance.<br />
<img src='http://chart.apis.google.com/chart?chxl=0:|Windows+2003|Windows+2008|RHEL+5.5|2:|Avg+M%2FSec|3:|Operating+System+(RHEL+shown+as+a+baseline+for+kernel+mode)&chxp=2,50|3,50&chxs=0,000000,10.5,0,l,676767|1,000000,11.5,0,l,676767|2,000000,11.5,0,l,676767|3,000000,11.5,0,l,676767&chxt=x,y,y,x&chbh=a,0,15&chs=800x300&cht=bvg&chco=4D89F9,C40000&chds=0,111,0,111&chd=t:66.74,35.62,111|111,111&chdl=MINT+served+directly+by+Tomcat+(not+using+HTTP.sys)|MINT+AJP+ISAPI+redirect+from+IIS+(using+HTTP.sys)&chtt=MINT+Comparison+-+Kernel+mode+(HTTP.sys)+vs.User+mode&chts=000000,16&graph.png' />
<br />
The tests also show that MINT does not add a significant amount of overhead to the transfer of binary items.<br />
<img src='http://chart.apis.google.com/chart?chxl=0:|Windows+2003|Windows+2008|RHEL+5.5|2:|Avg+M%2FSec|3:|Operating+System+(RHEL+shown+as+a+baseline+for+kernel+mode)&chxp=2,50|3,50&chxs=0,000000,10.5,0,l,676767|1,000000,11.5,0,l,676767|2,000000,11.5,0,l,676767|3,000000,11.5,0,l,676767&chxt=x,y,y,x&chbh=a,0,15&chs=800x300&cht=bvg&chco=4D89F9,C40000&chds=0,111,0,111&chd=t:70.78,50.16,112|111.8,112,112&chdl=Binaryitems+file+served+by+Apache+httpd+(not+using+HTTP.sys)|Binaryitems+file+served+by+FTP+(using+HTTP.sys+on+Windows+via+IIS)&chtt=Binary+transfer+Comparison+-+Kernel+mode+(HTTP.sys)+vs.User+mode&chts=000000,16&graph.png' />
<h2>Data Table</h2>
Test data results are available below for reference.  All values for runs represent average MB/sec over the entire transfer, except for the Average column which represents the average value of all runs.<br />
<table><thead><th> </th><th> <b>Average</b> </th><th> <b>Run 1</b> </th><th> <b>Run 2</b> </th><th> <b>Run 3</b> </th><th> <b>Run 4</b> </th><th> <b>Run 5</b> </th></thead><tbody>
<tr><td> <b>Windows 2003-MINT served directly by Tomcat</b> </td><td>66.74</td><td>67.7</td><td>67.5</td><td>65.9</td><td>67.1</td><td>65.5</td></tr>
<tr><td> <b>Windows 2003-Binaryitems file served by Apache httpd</b> </td><td>70.78</td><td>72.9</td><td>70.3</td><td>71.2</td><td>69.2</td><td>70.3</td></tr>
<tr><td> <b>Windows 2003-Binaryitems file served by IIS over FTP</b> </td><td>111.8</td><td>111</td><td>112</td><td>112</td><td>112</td><td>112</td></tr>
<tr><td> <b>Windows 2003-MINT AJP ISAPI redirect from IIS</b> </td><td>111</td><td>111</td><td>111</td><td>111</td><td>111</td><td>111</td></tr>
<tr><td> <b>Windows 2008-MINT served directly by Tomcat</b> </td><td>35.62</td><td>35.4</td><td>35.8</td><td>35.7</td><td>35.6</td><td>35.6</td></tr>
<tr><td> <b>Windows 2008-Binaryitems file served by Apache httpd</b> </td><td>50.16</td><td>50.1</td><td>49.8</td><td>50.5</td><td>49.6</td><td>50.8</td></tr>
<tr><td> <b>Windows 2008-Binaryitems file served by IIS over FTP</b> </td><td>112</td><td>112</td><td>112</td><td>112</td><td>112</td><td>112</td></tr>
<tr><td> <b>Windows 2008-MINT AJP ISAPI redirect from IIS</b> </td><td>111</td><td>111</td><td>111</td><td>111</td><td>111</td><td>111</td></tr>
<tr><td> <b>RHEL-Binaryitems file served by Apache httpd</b> </td><td>112</td><td>112</td><td>112</td><td>112</td><td>112</td><td>112</td></tr>
<tr><td> <b>RHEL-Binaryitems file served by vsftp</b> </td><td>112</td><td>112</td><td>112</td><td>112</td><td>112</td><td>112</td></tr>
<tr><td> <b>RHEL-MINT served directly by Tomcat</b> </td><td>111</td><td>111</td><td>111</td><td>111</td><td>111</td><td>111</td></tr></li></ul></tbody></table>

<hr />
<sup>1</sup> For more details on user/kernel mode switching performance problems: <a href='http://www.microsoft.com/technet/prodtechnol/WindowsServer2003/Library/IIS/a2a45c42-38bc-464c-a097-d7a202092a54.mspx?mfr=true'>HTTP Protocol Stack (IIS 6.0)</a>