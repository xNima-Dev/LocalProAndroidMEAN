import re

with open('/Users/mac/AndroidStudioProjects/LocalProAndroid/app/src/main/res/layout/fragment_earnings.xml', 'r') as f:
    content = f.read()

# Remove Recent Transactions from sectionMonth
pattern = re.compile(r'<!-- Recent Transactions Header -->.*?<!-- ============================== -->', re.DOTALL)
content = pattern.sub('<!-- ============================== -->', content)

with open('/Users/mac/AndroidStudioProjects/LocalProAndroid/app/src/main/res/layout/fragment_earnings.xml', 'w') as f:
    f.write(content)

