import urllib.request, re, json, zipfile, io, xml.etree.ElementTree as ET

url = "https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit"
req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"})
html = urllib.request.urlopen(req).read().decode("utf-8", errors="ignore")

print("HTML length:", len(html))

# Let's check regex for docs-sheet-tab-caption
tab_names = re.findall(r'docs-sheet-tab-caption\">([^<]+)<', html)
print("Found tab captions:", tab_names)

# Let's check the XLSX export
xlsx_url = "https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/export?format=xlsx"
xlsx_req = urllib.request.Request(xlsx_url, headers={"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"})
xlsx_data = urllib.request.urlopen(xlsx_req).read()
with zipfile.ZipFile(io.BytesIO(xlsx_data)) as z:
    wb_xml = z.read("xl/workbook.xml")
    root = ET.fromstring(wb_xml)
    print("XLSX sheets:")
    for elem in root.iter():
        if elem.tag.endswith("sheet") and "name" in elem.attrib:
            print(dict(elem.attrib))
