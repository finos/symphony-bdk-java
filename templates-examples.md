### Message data object properties

SIMPLE
```
{
  title: 'Simple Title',
  content: 'This is a simple message'
};
```
ALERT
```
{
  title: 'Alert Title',
  content: 'This is a danger alert'
};
```
INFORMATION
```
{
  title: 'Informaiton Title',
  content: 'This is a information message',
  description: 'Information message description'
};
```
NOTIFICATION
```
{
  title: 'My Title',
  content: 'My content',
  description: 'My description',
  comment: {
    body: 'My comments'
  },
  assignee: {
    displayName: 'John Smith'
  },
  showStatusBar: true,
  type: {
    name: 'message type'
  },
  priority: {
    name: 'message priority'
  },
  status: {
    name: 'message status'
  },
  labels: [
    {
      text: 'label1'
    },
    {
      text: 'label2'
    }
  ]
};
```
LIST
```
{
  title: 'Phones List',
  content: [
    "iPhone", "Samsung Galaxy", "Google Pixel 3"
  ]
};
```
TABLE
```
[
  { "Manufacturer": "Apple", "Phone": "iPhone", "Operating System": "iOS" },
  { "Manufacturer": "Samsung", "Phone": "Galaxy", "Operating System": "Android" },
  { "Manufacturer": "Google", "Phone": "Google Pixel 3", "Operating System": "Android" }
];
```
